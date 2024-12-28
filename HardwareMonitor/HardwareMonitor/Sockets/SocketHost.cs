using System.Net;
using System.Net.Sockets;
using Microsoft.Extensions.Logging;

namespace HardwareMonitor.Sockets;

public class SocketHost(ILogger logger)
{
    private Socket _listener;
    private List<Socket> _clients = new();
    private byte[] _receiveBuffer = new byte[2048];

    public Action<byte[]> onClientData;
    public Action onClientConnected;

    public async void StartServer()
    {
        IPEndPoint localEndPoint = new IPEndPoint(IPAddress.Any, 31337);
        logger.LogInformation("Listening for connections on {LocalEndPoint}", localEndPoint);

        _listener = new Socket(localEndPoint.AddressFamily, SocketType.Stream, ProtocolType.Tcp);
        _listener.Bind(localEndPoint);
        _listener.Listen();

        _listener.BeginAccept(OnConnection, _listener);
    }

    private void OnConnection(IAsyncResult asyncResult)
    {
        var server = (Socket)asyncResult.AsyncState!;
        var client = server.EndAccept(asyncResult);
        client.BeginReceive(_receiveBuffer, 0, _receiveBuffer.Length, SocketFlags.None, OnDataReceived, client);

        _clients.Add(client);
        server.BeginAccept(OnConnection, server);
        onClientConnected?.Invoke();
    }

    private void OnDataReceived(IAsyncResult asyncResult)
    {
        var client = (Socket)asyncResult.AsyncState!;
        int received = client.EndReceive(asyncResult);

        if (received > 0)
        {
            onClientData?.Invoke(_receiveBuffer);
        }
        
        client.BeginReceive(_receiveBuffer, 0, _receiveBuffer.Length, SocketFlags.None, OnDataReceived, client);
    }

    public void Close()
    {
        _clients.ForEach(it => it.Close());
        _listener.Close();
    }

    public bool HasConnections() => _clients.Count > 0;

    public void SendToAll(byte[] memoryStream)
    {
        var listWithSize = memoryStream.ToList();
        listWithSize.InsertRange(2, BitConverter.GetBytes(listWithSize.Count - 2));
        for (var i = 0; i < _clients.Count; i++)
        {
            if (_clients[i].IsConnected())
            {
                _clients[i].SendAsync(listWithSize.ToArray(), SocketFlags.None);
            }
        }
        listWithSize.Clear();
    }
}

static class SocketExtensions
{
    public static bool IsConnected(this Socket socket)
    {
        try
        {
            return !(socket.Poll(1, SelectMode.SelectRead) && socket.Available == 0);
        }
        catch (SocketException)
        {
            return false;
        }
    }
}