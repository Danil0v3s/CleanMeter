using System.Net;
using System.Net.Sockets;
using Microsoft.Extensions.Logging;

// ReSharper disable FieldCanBeMadeReadOnly.Local
#pragma warning disable CS8618 // Non-nullable field must contain a non-null value when exiting constructor. Consider adding the 'required' modifier or declaring as nullable.

namespace HardwareMonitor.Sockets;

public class SocketHost(ILogger logger)
{
    private Socket _listener;
    private List<Socket> _clients = [];
    private byte[] _receiveBuffer = new byte[2048];

    public Action<byte[]> OnClientData;
    public Action OnClientConnected;

    public void StartServer()
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
        logger.LogInformation("Accepted connection from {RemoteEndPoint}", client.RemoteEndPoint);
        client.BeginReceive(_receiveBuffer, 0, _receiveBuffer.Length, SocketFlags.None, OnDataReceived, client);

        _clients.Add(client);
        server.BeginAccept(OnConnection, server);
        OnClientConnected?.Invoke();
    }

    private void OnDataReceived(IAsyncResult asyncResult)
    {
        var client = (Socket)asyncResult.AsyncState!;
        try
        {
            int received = client.EndReceive(asyncResult);
            if (received > 0)
            {
                OnClientData?.Invoke(_receiveBuffer);
            }

            client.BeginReceive(_receiveBuffer, 0, _receiveBuffer.Length, SocketFlags.None, OnDataReceived, client);
        }
        catch
        {
            logger.LogInformation("Connection from {RemoteEndPoint} closed remotely", client.RemoteEndPoint);
            _clients.Remove(client);
            client.Dispose();
        }
    }

    public void Close()
    {
        logger.LogInformation("Closing all connections");
        _clients.ForEach(it => it.Close());
        _listener.Close();
    }

    public bool HasConnections() => _clients.Count > 0;

    public void SendToAll(byte[] memoryStream)
    {
        var listWithSize = memoryStream.ToList();
        listWithSize.InsertRange(2, BitConverter.GetBytes(listWithSize.Count - 2));
        foreach (var client in _clients)
        {
            try
            {
                client.SendAsync(listWithSize.ToArray(), SocketFlags.None);
            }
            catch (SocketException)
            {
                continue;
            }
        }

        listWithSize.Clear();
    }
}