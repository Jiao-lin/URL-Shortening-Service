package gm.url.shortening.controlador;

import com.fasterxml.jackson.annotation.JsonView;
import gm.url.shortening.entidad.Url;
import gm.url.shortening.servicio.IUrlServicio;
import gm.url.shortening.view.Views;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping("short")
@CrossOrigin(value = "http://localhost:3000")
public class UrlControlador {
    private static final Logger logger = LoggerFactory.getLogger(UrlControlador.class);

    @Autowired
    IUrlServicio iUrlServicio;

        @RequestMapping(path = "/{shortUrl}",method = RequestMethod.GET)
        @JsonView(Views.Public.class)
        public Object obtenerUrl(@PathVariable("shortUrl") String shortUrl){
            var obtener = iUrlServicio.obtenerUrl(shortUrl);
            obtener.setAccessCount(obtener.getAccessCount()+1);
            obtener.setUpdatedAt(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss zzz").format(new Date()));
            iUrlServicio.agregarUrl(obtener);
            return new RedirectView(obtener.getUrl());
            //return obtener;
        }

        @RequestMapping(path = "/{shortUrl}/stats",method = RequestMethod.GET)
        @JsonView(Views.Private.class)
        public Url obtenerUrlCompleta(@PathVariable("shortUrl") String shortUrl){
            try {
                return iUrlServicio.obtenerUrl(shortUrl);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    @GetMapping(path = "/get")
    public List<Url> listarUrls(){
       var res = iUrlServicio.listarUrl();
       res.forEach((e)-> logger.info(e.toString()));
       return res;
    }

    @RequestMapping(path = "/",method = RequestMethod.POST,params = "url")
    @JsonView(Views.Public.class)
    public Url agregarUrlShort(@RequestParam String url){
        final boolean[] bool = {true};
        Date date = new Date();
        Url url1 = new Url();

        // TODO CUIDADO CON ESTA FUNCION, PUEDE QUE NO FUNCIONE EL SERVER POSTERIORMENTE
        // Se listan todos los valores "short-URL"
        List<Url> lista = iUrlServicio.listarUrl();
        // Se genera un valor de 8 bits para el nuevo "short-URL"
        final String[] urlGen = {iUrlServicio.generarUrlShort()};
       while (bool[0]){
           lista.forEach((e)->{
               if (Objects.equals(urlGen[0], e.getShortCode())){
                   urlGen[0] =iUrlServicio.generarUrlShort();
               }else {
                   url1.setShortCode(urlGen[0]);
                   bool[0] =false;
               }
           });
       }
       SimpleDateFormat strFecha = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss zzz");

        url1.setUrl(url);
        url1.setCreatedAt(strFecha.format(date));
        url1.setUpdatedAt(strFecha.format(date));
        Url res = iUrlServicio.agregarUrl(url1);
        logger.info(res.toString());
        return res;
    }

    @RequestMapping(path = "/", method = RequestMethod.DELETE, params = "shortUrl")
    public ResponseEntity<Map<String,String>> borrarUrl(@RequestParam String shortUrl) {
        try {
            Url getUrl = iUrlServicio.obtenerUrl(shortUrl);
            iUrlServicio.borrarUrl(getUrl);
            logger.info(getUrl.toString() + " - Borrado con exito!");
            return ResponseEntity.status(204).body(Map.of("Message","Url borrada con exito"));
        }catch (Exception e){
            return ResponseEntity.status(404).body(Map.of("Error", "Enlace no encontrado"));
        }

    }
}
