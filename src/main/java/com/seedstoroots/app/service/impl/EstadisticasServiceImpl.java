package com.seedstoroots.app.service.impl;

import com.seedstoroots.app.dto.EstadisticasResponse;
import com.seedstoroots.app.repository.ProductoRepository;
import com.seedstoroots.app.repository.UsuarioRepository;
import com.seedstoroots.app.service.EstadisticasService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EstadisticasServiceImpl implements EstadisticasService {

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public EstadisticasServiceImpl(ProductoRepository productoRepository,
                                   UsuarioRepository usuarioRepository) {
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public EstadisticasResponse obtenerEstadisticas() {
        Long totalProductos = productoRepository.count();
        Long totalUsuarios = usuarioRepository.count();

        Long productosActivos = productoRepository.countByActivoTrue();
        Long usuariosActivos = usuarioRepository.countByActivoTrue();
        Long productosBajoStock = productoRepository.countByStockLessThan(10);

        return new EstadisticasResponse(
                totalProductos,
                totalUsuarios,
                productosActivos,
                usuariosActivos,
                productosBajoStock
        );
    }
}
