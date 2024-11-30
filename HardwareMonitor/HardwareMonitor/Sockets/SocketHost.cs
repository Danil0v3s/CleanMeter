using System.Net;
using System.Net.Sockets;
using Microsoft.Extensions.Logging;

namespace HardwareMonitor.Sockets;

public class SocketHost(ILogger logger)
{
    private Socket _listener;
    private List<Socket> _clients = new();
    
    public async void StartServer()
    {
        IPHostEntry host = Dns.GetHostEntry("127.0.0.1");
        IPAddress ipAddress = host.AddressList[0];
        IPEndPoint localEndPoint = new IPEndPoint(ipAddress, 31337);
        
        _listener = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);
        _listener.Bind(localEndPoint);
        _listener.Listen();

        _listener.BeginAccept(OnConnection, _listener);
    }

    private void OnConnection(IAsyncResult asyncResult)
    {
        var server = (Socket)asyncResult.AsyncState!;
        var client = server.EndAccept(asyncResult);
        
        _clients.Add(client);
        server.BeginAccept(OnConnection, server);
    }

    public void Close()
    {
        _clients.ForEach(it => it.Close());
        _listener.Close();
    }
    
    public bool HasConnections() => _clients.Count > 0;

    public void SendToAll(byte[] memoryStream)
    {
        for (var i = 0; i < _clients.Count; i++)
        {
            if (_clients[i].IsConnected())
            {
                _clients[i].SendAsync(memoryStream, SocketFlags.None);
            }
        }
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
        catch (SocketException) { return false; }
    }
}