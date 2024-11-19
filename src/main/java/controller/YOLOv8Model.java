package controller;

import ai.onnxruntime.*;
import java.util.Collections;

public class YOLOv8Model {
    private OrtEnvironment env;
    private OrtSession session;

    public YOLOv8Model(String modelPath) throws OrtException {
        env = OrtEnvironment.getEnvironment();
        session = env.createSession(modelPath, new OrtSession.SessionOptions());
    }

    public OrtSession.Result runOnnxModel(OnnxTensor inputTensor) throws OrtException {
        // Run the model on the input tensor
        return session.run(Collections.singletonMap("images", inputTensor));
    }




    public void close() throws OrtException {
        // Check if session is open before closing
        if (session != null) {
            session.close();
        }

        // Check if env is open before closing
        if (env != null) {
            env.close();
        }
    }

}
