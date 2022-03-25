package cz.cvut.fit.honysdan.converter;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import cz.cvut.fit.honysdan.entity.TrainerDTO;
import cz.cvut.fit.honysdan.resource.TrainerResource;

public class TrainerConverter implements Converter<TrainerDTO, Integer> {

    TrainerResource trainerResource;

    public TrainerConverter(TrainerResource trainerResource) {
        this.trainerResource = trainerResource;
    }

    @Override
    public Result<Integer> convertToModel(TrainerDTO value, ValueContext context) {
        try {
            return Result.ok(value.getId());
        } catch (Exception e) {
            return Result.ok(null);
        }
    }

    @Override
    public TrainerDTO convertToPresentation(Integer integer, ValueContext context) {
        if(integer != null) {
            return trainerResource.getTrainerById(integer);
        }
        return null;
    }
}