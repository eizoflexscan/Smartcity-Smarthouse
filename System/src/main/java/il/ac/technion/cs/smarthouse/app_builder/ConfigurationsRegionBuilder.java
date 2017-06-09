package il.ac.technion.cs.smarthouse.app_builder;

import java.util.Optional;

import il.ac.technion.cs.smarthouse.javafx_elements.AppBooleanButtonField;
import il.ac.technion.cs.smarthouse.javafx_elements.AppComboBoxField;
import il.ac.technion.cs.smarthouse.javafx_elements.AppSpinnerField;
import il.ac.technion.cs.smarthouse.javafx_elements.AppTextField;

/**
 * GUI layout - configurations region
 * <p>
 * A region for editable fields
 * @author RON
 * @since 10-06-2017
 */
public class ConfigurationsRegionBuilder extends RegionBuilder {
    public ConfigurationsRegionBuilder() {
        super.setTitle("Configurations");
    }
    
    @Override
    public ConfigurationsRegionBuilder setTitle(String title) {
        super.setTitle(title);
        return this;
    }
    
    public ConfigurationsRegionBuilder addStringInputField(String title, DataObject<String> bindingDataObject) {
        String initialValue = Optional.ofNullable(bindingDataObject.getData()).orElse("");
        addAppBuilderItem(new AppBuilderItem(title, new AppTextField(bindingDataObject::setData, initialValue)));
        return this;
    }

    public ConfigurationsRegionBuilder addDoubleInputField(String title, DataObject<Double> bindingDataObject) {
        double initialValue = Optional.ofNullable(bindingDataObject.getData()).orElse(0.0);
        addAppBuilderItem(new AppBuilderItem(title, AppSpinnerField.createDoubleAppSpinner(bindingDataObject::setData, initialValue)));
        return this;
    }

    public ConfigurationsRegionBuilder addIntegerInputField(String title, DataObject<Integer> bindingDataObject) {
        int initialValue = Optional.ofNullable(bindingDataObject.getData()).orElse(0);
        addAppBuilderItem(new AppBuilderItem(title, AppSpinnerField.createIntegerAppSpinner(bindingDataObject::setData, initialValue)));
        return this;
    }

    public <T> ConfigurationsRegionBuilder addComboboxInputField(String title, DataObject<T> bindingDataObject,
                    @SuppressWarnings("unchecked") T... comboOptions) {
        assert comboOptions.length > 0;
        T initialValue = Optional.ofNullable(bindingDataObject.getData()).orElse(comboOptions[0]);
        addAppBuilderItem(new AppBuilderItem(title, new AppComboBoxField<>(bindingDataObject::setData, initialValue, comboOptions)));
        return this;
    }

    public ConfigurationsRegionBuilder addButtonToggleField(String title, DataObject<Boolean> bindingDataObject) {
        boolean initialValue = Optional.ofNullable(bindingDataObject.getData()).orElse(false);
        addAppBuilderItem(new AppBuilderItem(title, new AppBooleanButtonField(bindingDataObject::setData, initialValue)));
        return this;
    }
}