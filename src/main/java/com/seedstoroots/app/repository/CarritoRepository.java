package com.seedstoroots.app.repository;

import com.seedstoroots.app.entity.Carrito;
import com.seedstoroots.app.entity.EstadoCarrito;
import com.seedstoroots.app.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByUsuarioAndEstado(Usuario usuario, EstadoCarrito estado);
    Optional<Carrito> findByUsuarioIdAndEstado(Long usuarioId, EstadoCarrito estado);
}