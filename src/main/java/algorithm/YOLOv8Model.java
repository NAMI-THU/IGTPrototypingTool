package algorithm;

import ai.onnxruntime.*;
import ai.onnxruntime.providers.OrtCUDAProviderOptions;

import java.util.Collections;

public class YOLOv8Model {
    private OrtEnvironment env;
    private OrtSession session;
    private boolean isSessionOpen;

    public YOLOv8Model(String modelPath) throws OrtException {
        env = OrtEnvironment.getEnvironment();

        //OrtCUDAProviderOptions cudaProviderOptions = new OrtCUDAProviderOptions(0);

        /*
        cudaProviderOptions.add("gpu_mem_limit","2147483648");
        cudaProviderOptions.add("arena_extend_strategy","kSameAsRequested");
        cudaProviderOptions.add("cudnn_conv_algo_search","DEFAULT");
        cudaProviderOptions.add("do_copy_in_default_stream","1");
        cudaProviderOptions.add("cudnn_conv_use_max_workspace","1");
        cudaProviderOptions.add("cudnn_conv1d_pad_to_nc1d","1");
        */

        OrtSession.SessionOptions sessionOptions = new OrtSession.SessionOptions();

        //sessionOptions.addCUDA();

        session = env.createSession(modelPath, sessionOptions);


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
