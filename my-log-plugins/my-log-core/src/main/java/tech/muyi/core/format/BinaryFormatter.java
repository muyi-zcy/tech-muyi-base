package tech.muyi.core.format;

import io.opentracing.propagation.*;
import tech.muyi.core.context.MySpanContext;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: muyi
 * @date: 2022/12/28
 **/
public class BinaryFormatter implements BaseFormatter<Binary> {
    @Override
    public Format<Binary> getFormatType() {
        return Format.Builtin.BINARY;
    }

    @Override
    public MySpanContext extract(Binary carrier) {
            String traceId = null;
        String spanId = null;
        Map<String, String> baggage = new HashMap<>();

        ObjectInputStream objStream = null;
        try {
            ByteBuffer extractBuff = carrier.extractionBuffer();
            byte[] buff = new byte[extractBuff.remaining()];
            extractBuff.get(buff);

            objStream = new ObjectInputStream(new ByteArrayInputStream(buff));
            spanId = objStream.readUTF();
            traceId = objStream.readUTF();

            while (objStream.available() > 0) {
                baggage.put(objStream.readUTF(), objStream.readUTF());
            }
        } catch (IOException e) {
            return MySpanContext.rootStart();
        } finally {
            if (objStream != null) {
                try {
                    objStream.close();
                } catch (Exception ignored) {
                }
            }
        }

        return new MySpanContext(traceId, spanId, baggage);

    }

    @Override
    public void inject(MySpanContext mySpanContext, Binary carrier) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutputStream objStream = null;
        try {
            objStream = new ObjectOutputStream(stream);
            objStream.writeUTF(mySpanContext.spanId());
            objStream.writeUTF(mySpanContext.traceId());

            for (Map.Entry<String, String> entry : mySpanContext.baggageItems()) {
                objStream.writeUTF(entry.getKey());
                objStream.writeUTF(entry.getValue());
            }
            objStream.flush();

            byte[] buff = stream.toByteArray();
            carrier.injectionBuffer(buff.length).put(buff);

        } catch (IOException e) {
            throw new RuntimeException("Corrupted state", e);
        } finally {
            if (objStream != null) {
                try {
                    objStream.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
