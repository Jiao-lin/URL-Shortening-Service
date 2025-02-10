package gm.url.shortening.repositorio;

import gm.url.shortening.entidad.Url;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUrlRepositorio extends JpaRepository<Url,String> {
}
