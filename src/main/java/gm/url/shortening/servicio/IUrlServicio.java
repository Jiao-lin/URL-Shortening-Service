package gm.url.shortening.servicio;

import gm.url.shortening.entidad.Url;

import java.util.List;

public interface IUrlServicio {
    public List<Url> listarUrl();
    public Url obtenerUrl(String url);
    public Url agregarUrl(Url url);
    public String generarUrlShort();
    public void borrarUrl(Url url);
}
