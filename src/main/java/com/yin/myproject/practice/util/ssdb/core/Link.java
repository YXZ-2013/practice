package com.yin.myproject.practice.util.ssdb.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Link {
    
    private Socket sock;
    private MemoryStream input = new MemoryStream();

    public Link(String host, int port) throws UnknownHostException, IOException {
        this(host, port, 0);
    }

    public Link(String host, int port, int timeout_ms) throws UnknownHostException, IOException {
        sock = new Socket(host, port);
        if (timeout_ms > 0) {
            sock.setSoTimeout(timeout_ms);
        }
        sock.setTcpNoDelay(true);

    }

    public void close() {
        try {
            sock.close();
        } catch (Exception e) {
            //
        }
    }

    public boolean isConnected() {
        try {
            return sock.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isClosed() {
        try {
            return sock.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public Response request(String cmd, String hCmd, byte[]... params) throws IOException {
        ArrayList<byte[]> list = new ArrayList<byte[]>();
        list.add(hCmd.getBytes());
        for (byte[] s : params) {
            list.add(s);
        }
        return this.request(cmd, list);
    }

    public Response request(String cmd, byte[]... params) throws IOException {
        ArrayList<byte[]> list = new ArrayList<byte[]>();
        for (byte[] s : params) {
            list.add(s);
        }
        return this.request(cmd, list);
    }

    public Response request(String cmd, String... params) throws IOException {
        ArrayList<byte[]> list = new ArrayList<byte[]>();
        for (String s : params) {
            list.add(s.getBytes());
        }
        return this.request(cmd, list);
    }

    public Response request(String cmd, List<byte[]> params) throws IOException {
        MemoryStream buf = new MemoryStream(4096);
        Integer len = cmd.length();
        buf.write(len.toString());
        buf.write('\n');
        buf.write(cmd);
        buf.write('\n');
        for (byte[] bs : params) {
            len = bs.length;
            buf.write(len.toString());
            buf.write('\n');
            buf.write(bs);
            buf.write('\n');
        }
        buf.write('\n');
        send(buf);

        List<byte[]> list = recv();
        return new Response(list);
    }

    private void send(MemoryStream buf) throws IOException {
        // System.out.println(">> " + buf.printable());
        OutputStream os = sock.getOutputStream();
        os.write(buf.buf, buf.data, buf.size);
        os.flush();
    }

    private List<byte[]> recv() throws IOException {
        input.nice();
        InputStream is = sock.getInputStream();
        while (true) {
            List<byte[]> ret = parse();
            if (ret != null) {
                return ret;
            }
            byte[] bs = new byte[8192];
            int len = is.read(bs);
            // System.out.println("<< " + (new MemoryStream(bs, 0, len)).printable());
            input.write(bs, 0, len);
        }
    }

    private List<byte[]> parse() {
        ArrayList<byte[]> list = new ArrayList<byte[]>();
        byte[] buf = input.buf;

        int idx = 0;
        while (true) {
            int pos = input.memchr('\n', idx);
            // System.out.println("pos: " + pos + " idx: " + idx);
            if (pos == -1) {
                break;
            }
            if (pos == idx || (pos == idx + 1 && buf[idx] == '\r')) {
                // ignore empty leading lines
                if (list.isEmpty()) {
                    idx += 1; // if '\r', next time will skip '\n'
                    continue;
                } else {
                    input.decr(idx + 1);
                    return list;
                }
            }
            String str = new String(buf, input.data + idx, pos - idx);
            int len = Integer.parseInt(str);
            idx = pos + 1;
            if (idx + len >= input.size) {
                break;
            }
            byte[] data = Arrays.copyOfRange(buf, input.data + idx, input.data + idx + len);
            // System.out.println("len: " + len + " data: " + data.length);
            idx += len + 1; // skip '\n'
            list.add(data);
        }
        return null;
    }

    public String info() {
        return host() + ":" + port() + ":" + timeout();
    }

    public String host() {
        return sock.getInetAddress().getHostAddress();
    }

    public int port() {
        return sock.getPort();
    }

    public int timeout() {
        try {
            return sock.getSoTimeout();
        } catch (SocketException e) {
            return -1;
        }
    }
}
