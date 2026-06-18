package com.fitchain.reserva.assembler;


import com.fitchain.reserva.controller.ReservaController;
import com.fitchain.reserva.dto.ReservaResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ReservaModelAssembler implements RepresentationModelAssembler<ReservaResponseDTO, EntityModel<ReservaResponseDTO>> {
    @Override
    public EntityModel<ReservaResponseDTO> toModel(ReservaResponseDTO dto) {
        return EntityModel.of(
                dto, linkTo(methodOn(ReservaController.class).obtenerPorId(dto.getId())).withSelfRel()
                , linkTo(methodOn(ReservaController.class).actualizar(dto.getId(),null)).withRel("update"),
                linkTo(methodOn(ReservaController.class).eliminar(dto.getId())).withRel("delete"));
    }
}
