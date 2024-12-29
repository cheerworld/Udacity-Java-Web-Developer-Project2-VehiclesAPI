package com.udacity.vehicles.api;

import com.udacity.vehicles.domain.car.Car;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * Maps the CarController to the Car class using HATEOAS
 */
@Component
public class CarResourceAssembler extends RepresentationModelAssemblerSupport<Car, EntityModel<Car>> {


    public CarResourceAssembler() {
        super(CarController.class, (Class<EntityModel<Car>>) (Class<?>) EntityModel.class);
    }


    @Override
    public EntityModel<Car> toModel(Car car) {
        return EntityModel.of(car,
                linkTo(methodOn(CarController.class).get(car.getId())).withSelfRel(),
                linkTo(methodOn(CarController.class).list()).withRel("cars"));
    }
}
