package jp.jaxa.iss.kibo.rpc.sampleapk;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.sampleapk.ai.LiteModelLoader;

import java.io.IOException;


/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee.
 */

public class YourService extends KiboRpcService {
    @Override
    protected void runPlan1(){
        // The mission starts.
        api.startMission();

        // Smooth movement to a point.
        Point target = new Point(10.9d, -9.92284d, 5.195d);
        Quaternion orientation = new Quaternion(0f, 0f, -0.707f, 0.707f);
        smoothMoveTo(target, orientation, 1);  // ใช้การเคลื่อนที่แบบลื่น

        // Get a camera image.
//        Mat image = api.getMatNavCam();
//        try {
////            ItemClassifier classifier = new ItemClassifier(context.getAssets());
////            Mat image = api.getMatNavCam();
////            if (image != null) {
////                String itemName = classifier.classify(image);
////                api.setAreaInfo(1, itemName, 1);
////            }
////        } catch (IOException e) {
////            api.log("AI error: " + e.getMessage());
////        }
        try {
            String modelPath = "app/src/main/assets/my_model_float32.tflite"; // ให้แน่ใจว่า path นี้มีจริง
            LiteModelLoader model = new LiteModelLoader(modelPath);
            api.setAreaInfo(1, "itemFond", 1);  // ตั้งชื่อ class แบบชั่วคราว

            // ตัวอย่าง input ขนาด [1, 224, 224, 3] (float32) สำหรับภาพ RGB
            float[][][][] input = new float[1][224][224][3];
            // 🧠 TODO: แปลงภาพ NavCam ใส่ input ตรงนี้

            float[] output = model.predict(input);

            // หาค่าความมั่นใจสูงสุด
            int bestIndex = 0;
            float bestProb = output[0];
            for (int i = 1; i < output.length; i++) {
                if (output[i] > bestProb) {
                    bestProb = output[i];
                    bestIndex = i;
                }
            }



        } catch (IOException e) {
            api.setAreaInfo(1, "itemNoFond", 1);  // ตั้งชื่อ class แบบชั่วคราว
        }
        /* ******************************************************************************** */
        /* Write your code to recognize the type and number of landmark items in each area! */
        /* If there is a treasure item, remember it.                                        */
        /* ******************************************************************************** */

        // When you recognize landmark items, let’s set the type and number.
//        api.setAreaInfo(1, "item_name", 1);

        /* **************************************************** */
        /* Let's move to each area and recognize the items. */
        /* **************************************************** */

        // When you move to the front of the astronaut, report the rounding completion.
//        target = new Point(11.143d, -6.7607d, 4.9654d);
//        orientation = new Quaternion(0f, 0f, 0.707f, 0.707f);
//        api.moveTo(target, orientation, true);
        api.reportRoundingCompletion();

        /* ********************************************************** */
        /* Write your code to recognize which target item the astronaut has. */
        /* ********************************************************** */

        // Let's notify the astronaut when you recognize it.
        api.notifyRecognitionItem();

        /* ******************************************************************************************************* */
        /* Write your code to move Astrobee to the location of the target item (what the astronaut is looking for) */
        /* ******************************************************************************************************* */

        // Take a snapshot of the target item.
        api.takeTargetItemSnapshot();
    }

    @Override
    protected void runPlan2(){
        // write your plan 2 here.
    }

    @Override
    protected void runPlan3(){
        // write your plan 3 here.
    }

    // Smooth move method
    private void smoothMoveTo(Point target, Quaternion orientation, int steps) {
        Point start = api.getRobotKinematics().getPosition();

        for (int i = 1; i <= steps; i++) {
            double alpha = (double)i / steps;
            double x = (1 - alpha) * start.getX() + alpha * target.getX();
            double y = (1 - alpha) * start.getY() + alpha * target.getY();
            double z = (1 - alpha) * start.getZ() + alpha * target.getZ();

            Point intermediate = new Point(x, y, z);
            api.moveTo(intermediate, orientation, true);
        }
    }

    // You can add your method.
    private String yourMethod(){
        return "your method";
    }

}
