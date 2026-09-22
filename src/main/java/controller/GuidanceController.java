package controller;

public interface GuidanceController extends Controller {

    /**
     * This method makes sure, every {@link GuidanceController} sets it's instance of the
     * {@link GuidanceHandler}. This is needed, because after content switch in the {@link MainController}
     * the {@link GuidanceController} is a new instance.
     * */
    void setGuidanceHandler(GuidanceHandler guidanceHandler);
}
