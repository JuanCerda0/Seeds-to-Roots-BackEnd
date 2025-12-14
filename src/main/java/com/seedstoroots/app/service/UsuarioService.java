package com.seedstoroots.app.service;

import com.seedstoroots.app.dto.UsuarioCreateRequest;
import com.seedstoroots.app.dto.UsuarioResponse;
import com.seedstoroots.app.dto.UsuarioUpdateRequest;
import java.util.List;

public interface UsuarioService {
    UsuarioResponse crear(UsuarioCreateRequest request);
    List<UsuarioResponse> obtenerTodos();
    UsuarioResponse obtenerPorId(Long id);
    UsuarioResponse obtenerPorEmail(String email);
    UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request);
    void eliminar(Long id);
}
