package com.Repositorios;

import com.Entidades.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class RepositorioUsuario implements PanacheRepository<Usuario> {

    @Inject
    EntityManager em;

    public Usuario findByUsername(String username) {
        return find("correo", username).firstResult();
    }

    // Guardar uno o varios usuarios
    @Transactional
    public void save(Usuario... usuarios) {
        for (Usuario usuario : usuarios) {
            em.persist(usuario);
        }
    }

    @Transactional
    public boolean checkDatabaseConnection() {
        try {
            Query query = em.createNativeQuery("SELECT 1");
            query.getSingleResult();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
