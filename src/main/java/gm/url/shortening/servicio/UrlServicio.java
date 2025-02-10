package gm.url.shortening.servicio;

import gm.url.shortening.entidad.Url;
import gm.url.shortening.repositorio.IUrlRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

@Service
public class UrlServicio implements IUrlServicio{
    @Autowired
    IUrlRepositorio urlRepositorio;

    @Override
    public List<Url> listarUrl() {
        return urlRepositorio.findAll();
    }

    @Override
    public Url obtenerUrl(String url) {
       return urlRepositorio.findById(url).orElse(null);
    }

    @Override
    public Url agregarUrl(Url url) {
        return urlRepositorio.save(url);
    }

    @Override
    public String generarUrlShort() {
        UUID uuid = UUID.randomUUID();
        return new BigInteger(uuid.toString().replace("-", ""), 16).toString(36).substring(0, 8);
    }

    @Override
    public void borrarUrl(Url url) {
        urlRepositorio.delete(url);
    }

}
