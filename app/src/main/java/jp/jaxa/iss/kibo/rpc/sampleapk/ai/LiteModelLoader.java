package jp.jaxa.iss.kibo.rpc.sampleapk.ai;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class LiteModelLoader {
    private Interpreter tflite;

    // ✅ Constructor: โหลดโมเดลจาก path โดยใช้ FileInputStream
    public LiteModelLoader(String modelPath) throws IOException {
        MappedByteBuffer buffer = loadModelFile(modelPath);
        tflite = new Interpreter(buffer);
    }

    // ✅ โหลดไฟล์และ map เป็น buffer
    private MappedByteBuffer loadModelFile(String path) throws IOException {
        File file = new File(path);
        FileInputStream inputStream = new FileInputStream(file);
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = 0;
        long declaredLength = fileChannel.size();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // ✅ เรียกใช้งานโมเดล
    public float[] predict(float[][][][] input) {
        float[][] output = new float[1][11];  // ปรับตามจำนวน class
        tflite.run(input, output);
        return output[0];
    }

    // ✅ ปิด interpreter เมื่อใช้เสร็จ
    public void close() {
        if (tflite != null) {
            tflite.close();
        }
    }
}
