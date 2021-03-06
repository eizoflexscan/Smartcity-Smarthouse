package il.ac.technion.cs.smarthouse.mvp;

import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import il.ac.technion.cs.smarthouse.utils.BoolLatch;
import il.ac.technion.cs.smarthouse.utils.TimeCounter;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;

/**
 * An abstruct controller class for JavaFx's controllers.
 * <p>
 * This controller class support a specific model that is used by all
 * sub-controllers that are created by the root controller.
 * <p>
 * The idea is that the main controller will be a root controller (created by
 * {@link GuiController#createRootController(URL, Object)}, and all the other
 * controllers will be created by him (with
 * {@link GuiController#createChildController(URL)})
 * 
 * @author RON
 * @since 07-06-2017
 */
public abstract class GuiController<M, D> implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(GuiController.class);

    private GuiController<M, D> parent;
    private M model;
    private Parent rootViewNode;
    private D extraData;
    private final BoolLatch startedLatch = new BoolLatch();

    @Override
    public final void initialize(final URL location, final ResourceBundle b) {}

    /**
     * This function will be called by
     * {@link #loadPresenter(FXMLLoader, Object, Object, GuiController)} when
     * the controller is initialized
     * 
     * @param model1
     *            the model that was passed by the parent (by
     *            {@link GuiController#createChildController(URL)}) or by the
     *            user (if this is the root controller)
     * @param location
     *            location of the FXML
     * @param b
     *            resources
     */
    protected abstract <T extends GuiController<M, D>> void initialize(M model1, T parent1, D extraData1, URL location,
                    ResourceBundle b);

    /**
     * Block until the function
     * {@link GuiController#initialize(Object, URL, ResourceBundle)} is returned
     */
    public final void waitUntilInitFinishes() {
        final TimeCounter t = new TimeCounter().start();
        startedLatch.blockUntilTrue();
        log.info("GuiController: Waited " + t.getTimePassedMillis() + " [ms] for controller to load");
    }

    private final void notifyOnLoaded() {
        log.trace("Loaded GuiController: " + getClass().getName());
        startedLatch.setTrueAndRelease();
    }

    /**
     * Get the root node created by the fxml
     * 
     * @return the parent node
     */
    public final Parent getRootViewNode() {
        return rootViewNode;
    }

    /**
     * Get the parent controller
     * 
     * @return the parent controller (or null if this is the root controller)
     */
    @SuppressWarnings("unchecked")
    protected <T extends GuiController<M, D>> T getParentController() {
        return (T) parent;
    }

    /**
     * Get the model
     * 
     * @return the model
     */
    protected final M getModel() {
        assert model != null;
        return model;
    }

    protected final D getExtraData() {
        return extraData;
    }

    /**
     * Loads a new fxml (that should create a {@code GuiController<ModelType>}
     * controller). The given parent and model are passed to the new controller
     * 
     * @param l
     * @param model1
     * @param parent
     * @return the new controller
     */
    @SuppressWarnings("unchecked")
    private static <ModelType, ExtraDataType, T extends GuiController<ModelType, ExtraDataType>> T loadPresenter(
                    final FXMLLoader l, final ModelType model1, final ExtraDataType extraData1,
                    final GuiController<ModelType, ExtraDataType> parent) {
        assert l != null;
        assert model1 != null;

        try {
            final Parent p = l.load();
            final Object child = l.getController();

            if (!GuiController.class.isAssignableFrom(child.getClass()))
                throw new Exception("Child (" + child.getClass() + ") must extend " + GuiController.class);

            GuiController<ModelType, ExtraDataType> c = (GuiController<ModelType, ExtraDataType>) child;
            c.model = model1;
            c.parent = parent;
            c.extraData = extraData1;
            c.rootViewNode = p;

            c.initialize(c.getModel(), c.getParentController(), c.getExtraData(), l.getLocation(), l.getResources());
            c.notifyOnLoaded();

            return (T) c;
        } catch (Exception e) {
            String r = GuiController.class.getName() + " can't load the FXML: " + l.getLocation();
            log.error(r, e);
            throw new RuntimeException(r);
        }
    }

    /**
     * Create a root controller
     * <p>
     * Uses {@link #loadPresenter(FXMLLoader, Object, GuiController)}
     * 
     * @param fxmlLocation
     * @param model1
     * @return the new controller
     */
    public static <ModelType, ExtraDataType, T extends GuiController<ModelType, ExtraDataType>> T createRootController(
                    final URL fxmlLocation, final ModelType model1, ExtraDataType extraData1) {
        return loadPresenter(new FXMLLoader(fxmlLocation), model1, extraData1, null);
    }

    /**
     * Create a child controller
     * <p>
     * Uses {@link #loadPresenter(FXMLLoader, Object, GuiController)}
     * 
     * @param fxmlLocation
     * @return the new controller
     */
    protected final <T extends GuiController<M, D>> T createChildController(final URL fxmlLocation) {
        return loadPresenter(new FXMLLoader(fxmlLocation), getModel(), extraData, this);
    }
}
