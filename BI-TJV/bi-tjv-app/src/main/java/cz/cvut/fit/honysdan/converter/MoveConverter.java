package cz.cvut.fit.honysdan.converter;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import cz.cvut.fit.honysdan.entity.MoveDTO;
import cz.cvut.fit.honysdan.resource.MoveResource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MoveConverter implements Converter<Set<MoveDTO>, List<Integer>> {

    MoveResource moveResource;

    public MoveConverter(MoveResource moveResource) {
        this.moveResource = moveResource;
    }

    @Override
    public Result<List<Integer>> convertToModel(Set<MoveDTO> value, ValueContext context) {
        return Result.ok(value.stream().map(MoveDTO::getId).collect(Collectors.toList()));
    }

    @Override
    public Set<MoveDTO> convertToPresentation(List<Integer> value, ValueContext context) {
        if(value != null) {
            List<MoveDTO> list = new ArrayList<>();
            for(Integer id : value) {
                list.add(moveResource.getMoveById(id));
            }
            return new HashSet<>(list);
        }
        return null;
    }
}