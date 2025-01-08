package controller;

import ai.onnxruntime.*;
import java.util.Collections;

public class YOLOv8Model {
    private OrtEnvironment env;
    private OrtSession session;
    private boolean isSessionOpen; // Flag to track session state

    public YOLOv8Model(String modelPath) throws OrtException {
        env = OrtEnvironment.getEnvironment();
        session = env.createSession(modelPath, new OrtSession.SessionOptions());
        isSessionOpen = true; // Initially the session is open
    }

    public OrtSession.Result runOnnxModel(OnnxTensor inputTensor) throws OrtException {
        // Check if session is open before running the model
        if (!isSessionOpen) {
            throw new OrtException("Session is closed, cannot run the model.");
        }
        // Run the model on the input tensor
        return session.run(Collections.singletonMap("images", inputTensor));
    }

    public void close() throws OrtException {
        // Check if session is open before closing
        if (session != null) {
            session.close();
            isSessionOpen = false; // Update the flag when session is closed
        }

        // Check if env is open before closing
        if (env != null) {
            env.close();
        }
    }

    // Method to check if the session is open
    public boolean isSessionOpen() {
        return isSessionOpen;
    }
}
