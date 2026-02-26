package algorithm;

import ai.onnxruntime.*;
import java.util.Collections;

public class YOLOv8Model {
    private OrtEnvironment env;
    private OrtSession session;
    private boolean isSessionOpen;

    public YOLOv8Model(String modelPath) throws OrtException {
        env = OrtEnvironment.getEnvironment();
        session = env.createSession(modelPath, new OrtSession.SessionOptions());
        isSessionOpen = true;
    }

    public OrtSession.Result runOnnxModel(OnnxTensor inputTensor) throws OrtException {
        if (!isSessionOpen) {
            throw new OrtException("Session is closed, cannot run the model.");
        }
        return session.run(Collections.singletonMap("images", inputTensor));
    }

    public void close() throws OrtException {
        if (session != null) {
            session.close();
            isSessionOpen = false;
        }

        if (env != null) {
            env.close();
        }
    }

    public boolean isSessionOpen() {
        return isSessionOpen;
    }
}
