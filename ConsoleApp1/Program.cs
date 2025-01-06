// See https://aka.ms/new-console-template for more information

using System.Net;
using System.Net.Sockets;

await (new TestClass()).Main();

enum Command : short
{
    Data,
    RefreshPresentMonApps,
    SelectPresentMonApp,
    PresentMonApps,
    SelectPollingRate
}

public class TestClass
{
    byte[] buffer = new byte[500_000];

    public async Task Main()
    {
        IPHostEntry host = Dns.GetHostEntry("127.0.0.1");
        IPAddress ipAddress = host.AddressList[0];
        IPEndPoint localEndPoint = new IPEndPoint(ipAddress, 31337);

        var socket = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);
        await socket.ConnectAsync(localEndPoint);
        var buffer = new byte[500_000];

        while (true)
        {
            // Receive ack.
            var received = await socket.ReceiveAsync(buffer, SocketFlags.None);
            var command = (Command) BitConverter.ToInt16(buffer, 0);
            Console.WriteLine($"Received {command} {received} bytes");
        }
    }
}