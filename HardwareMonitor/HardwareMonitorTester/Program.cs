// See https://aka.ms/new-console-template for more information

using System.Net;
using System.Net.Sockets;
using HardwareMonitor.Monitor;

await (new TestClass()).Main();

public class TestClass
{
    byte[] buffer = new byte[500_000];

    public async Task Main()
    {
        var ipAddress = IPAddress.Loopback;
        var localEndPoint = new IPEndPoint(ipAddress, 31337);

        var socket = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);
        await socket.ConnectAsync(localEndPoint);
        var buffer = new byte[500_000];

        while (true)
        {
            // Receive ack.
            var received = await socket.ReceiveAsync(buffer, SocketFlags.None);
            var command = (MonitorPacketCommand)BitConverter.ToInt16(buffer, 0);
            Console.WriteLine($"Received {command} {received} bytes");
        }
    }
}