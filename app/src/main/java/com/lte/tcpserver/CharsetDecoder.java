package com.lte.tcpserver;

import android.util.Log;

import com.lte.ui.tagview.Utils;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenxiaojun on 2017/8/28.
 */

public class CharsetDecoder extends CumulativeProtocolDecoder {

    private static String TAG = "CharsetDecoder";

    private final static Charset charset = Charset.forName("UTF-8");

    private final AttributeKey BUFFER = new AttributeKey(getClass(), "buffer");

    // 可变的IoBuffer数据缓冲区
    private IoBuffer buff = IoBuffer.allocate(1024).setAutoExpand(true);

    private int Height;

    private int Low;

    private int start = 0;

    private int length;

//    @Override
//    public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
//
//        if (!session.getTransportMetadata().hasFragmentation()) {
//            while (in.hasRemaining()) {
//                // 判断是否符合解码要求，不符合则中断并返回
//                if (!doDecode(session, in, out)) {
//                    break;
//                }
//            }
//            return;
//        }
//
//        boolean usingSessionBuffer = true;
//        // 取得上次断包数据
//        IoBuffer buf = (IoBuffer) session.getAttribute(BUFFER);
//        // If we have a session buffer, append data to that; otherwise
//        // use the buffer read from the network directly.
//        if (buf != null) { // 如果有断包数据
//            boolean appended = false;
//            // Make sure that the buffer is auto-expanded.
//            if (buf.isAutoExpand()) {
//                try {
//                    // 将断包数据和当前传入的数据进行拼接
//                    buf.put(in);
//                    appended = true;
//                } catch (IllegalStateException e) {
//                    // A user called derivation method (e.g. slice()),
//                    // which disables auto-expansion of the parent buffer.
//                } catch (IndexOutOfBoundsException e) {
//                    // A user disabled auto-expansion.
//                }
//            }
//
//            if (appended) {
//                buf.flip();// 如果是拼接的数据，将buf置为读模式
//            } else {
//                // Reallocate the buffer if append operation failed due to
//                // derivation or disabled auto-expansion.
//                //如果buf不是可自动扩展的buffer，刚通过数据拷贝的方式将断包数据和当前数据进行拼接
//                buf.flip();
//                IoBuffer newBuf = IoBuffer.allocate(buf.remaining() + in.remaining()).setAutoExpand(true);
//                newBuf.order(buf.order());
//                newBuf.put(buf);
//                newBuf.put(in);
//                newBuf.flip();
//                buf = newBuf;
//
//                // Update the session attribute.
//                session.setAttribute(BUFFER, buf);
//            }
//        } else {
//            buf = in;
//            usingSessionBuffer = false;
//        }
//
//        for (;;) {
//            int oldPos = buf.position();
//            boolean decoded = doDecode(session, buf, out);// 进行数据的解码操作
//            if (decoded) {
//                // 如果符合解码要求并进行了解码操作，
//                // 则当前position和解码前的position不可能一样
//                if (buf.position() == oldPos) {
//                    throw new IllegalStateException("doDecode() can't return true when buffer is not consumed.");
//                }
//                // 如果已经没有数据，则退出循环
//                if (!buf.hasRemaining()) {
//                    break;
//                }
//            } else {// 如果不符合解码要求，则退出循环
//                break;
//            }
//        }
//        // if there is any data left that cannot be decoded, we store
//        // it in a buffer in the session and next time this decoder is
//        // invoked the session buffer gets appended to
//        if (buf.hasRemaining()) {
//            if (usingSessionBuffer && buf.isAutoExpand()) {
//                buf.compact();
//            } else {
//                //如果还有没处理完的数据（一般为断包），刚将此数据存入session中，以便和下次数据进行拼接。
//                storeRemainingInSession(buf, session);
//            }
//        } else {
//            if (usingSessionBuffer) {
//                removeSessionBuffer(session);
//            }
//        }
//    }

    public boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
        // 如果有消息
        if(in.remaining() > 4){//前4字节是包头
            //标记当前position的快照标记mark，以便后继的reset操作能恢复position位置
            in.mark();
            byte[] l = new byte[4];
            in.get(l);

            //包体数据长度
            int len = Utils.bytes2int(l);//将byte转成int


            //注意上面的get操作会导致下面的remaining()值发生变化
            if(in.remaining() < len){
                //如果消息内容不够，则重置恢复position位置到操作前,进入下一轮, 接收新数据，以拼凑成完整数据
                in.reset();
                return false;
            }else{
                //消息内容足够
                in.reset();//重置恢复position位置到操作前
                int sumlen = 4+len;//总长 = 包头+包体
                byte[] packArr = new byte[sumlen];
                in.get(packArr, 0 , sumlen);

                IoBuffer buffer = IoBuffer.allocate(sumlen);
                buffer.put(packArr);
                buffer.flip();
                ArrayList<Integer> list = new ArrayList<>();
                for (byte aByte : buffer.array()) {
                    list.add((int) aByte);
                }
                out.write(list);
                buffer.free();

                if(in.remaining() > 0){//如果读取一个完整包内容后还粘了包，就让父类再调用一次，进行下一次解析
                    return true;
                }
            }
        }
        return false;//处理成功，让父类进行接收下个包
    }

    private void removeSessionBuffer(IoSession session) {
        session.removeAttribute(BUFFER);
    }

    private void storeRemainingInSession(IoBuffer buf, IoSession session) {
        final IoBuffer remainingBuf = IoBuffer.allocate(buf.capacity()).setAutoExpand(true);

        remainingBuf.order(buf.order());
        remainingBuf.put(buf);

        session.setAttribute(BUFFER, remainingBuf);
    }
    @Override
    public void dispose(IoSession session) throws Exception {
        Log.d(TAG,"#########dispose#########");
        Log.d(TAG,"============"+session.getCurrentWriteMessage());
    }

    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
        Log.d(TAG,"#########完成解码#########");
    }
}
