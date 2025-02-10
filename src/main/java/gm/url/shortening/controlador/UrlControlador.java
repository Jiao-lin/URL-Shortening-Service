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
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss zzz");

    @Autowired
    IUrlServicio iUrlServicio;

    @RequestMapping(path = "/{shortUrl}",method = RequestMethod.GET)
    @JsonView(Views.Public.class)
    public ResponseEntity<Object> obtenerUrl(@PathVariable("shortUrl") String shortUrl){
        try {
            var obtener = iUrlServicio.obtenerUrl(shortUrl);
            obtener.setUpdatedAt(simpleDateFormat.format(new Date()));
            iUrlServicio.agregarUrl(obtener);
            return ResponseEntity.status(200).body(obtener);
        }catch (Exception e){
            return ResponseEntity.status(404).body(Map.of("Error", "Enlace no encontrado"));
            }
    }

    @RequestMapping(path = "/{shortUrl}/stats",method = RequestMethod.GET)
    @JsonView(Views.Private.class)

    public ResponseEntity<Object> obtenerUrlCompleta(@PathVariable("shortUrl") String shortUrl){
        try {
            return ResponseEntity.status(200).body(iUrlServicio.obtenerUrl(shortUrl));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("Error", "Enlace no encontrado"));
        }
    }

    @GetMapping(path = "/all")
    @JsonView(Views.Private.class)
    public List<Url> listarUrls(){
            var res = iUrlServicio.listarUrl();
            res.forEach((e)-> logger.info(e.toString()));
            return res;
    }

    @RequestMapping(path = "",method = RequestMethod.POST)
    @JsonView(Views.Public.class)
    public ResponseEntity<Object> agregarUrlShort(@RequestParam String url){
        final boolean[] bool = {true};
        Date date = new Date();
        Url url1 = new Url();

        if (!url.startsWith("https://") || url.isBlank()){

            return ResponseEntity.status(400).body(Map.of("Error","Enlace proporcionado invalido"));

        }else {

            // TODO CUIDADO CON ESTA FUNCION, PUEDE QUE NO FUNCIONE EL SERVER POSTERIORMENTE
            // Se listan todos los valores "short-URL"
            List<Url> lista = iUrlServicio.listarUrl();
            // Se genera un valor de 8 bits para el nuevo "short-URL"
            final String[] urlGen = {iUrlServicio.generarUrlShort()};

            while (bool[0]){
                lista.forEach((e)->{
                    if (Objects.equals(urlGen[0], e.getShortCode())){
                        urlGen[0] =iUrlServicio.generarUrlShort();
                    } else {
                        url1.setShortCode(urlGen[0]);
                        bool[0] =false;
                    }
                });
            }

            url1.setUrl(url);
            url1.setCreatedAt(simpleDateFormat.format(date));
            url1.setUpdatedAt(simpleDateFormat.format(date));
            Url res = iUrlServicio.agregarUrl(url1);
            logger.info(res.toString());

            return ResponseEntity.status(201).body(res);

        }

    }

    @RequestMapping(path = "/{shortUrl}", method = RequestMethod.PUT)
    @JsonView(Views.Public.class)
    public ResponseEntity<Object> modificarUrl( @PathVariable String shortUrl, @RequestParam String url ) {
        if (!url.startsWith("https://") || url.isBlank()) {

            return ResponseEntity.status(400).body(Map.of("Error","Enlace proporcionado invalido"));

        } else {
            try {
                Url url1 = iUrlServicio.obtenerUrl(shortUrl);
                url1.setUrl(url);
                url1.setAccessCount(url1.getAccessCount()+1);
                logger.info("LA URL ES: "+url);
                Url res = iUrlServicio.agregarUrl(url1);

                return ResponseEntity.status(200).body(res);
            } catch (Exception e) {
                return ResponseEntity.status(404).body(Map.of("Error", "Enlace no encontrado"));
            }
        }
    }

    @RequestMapping(path = "", method = RequestMethod.DELETE)
    public ResponseEntity<Object> borrarUrl(@RequestParam String shortUrl) {
        try {
            Url getUrl = iUrlServicio.obtenerUrl(shortUrl);
            iUrlServicio.borrarUrl(getUrl);
            logger.info(getUrl.toString() + " - Borrado con exito!");

            return ResponseEntity.noContent().build();

        }catch (Exception e){
            return ResponseEntity.status(404).body(Map.of("Error", "Enlace no encontrado"));
        }
    }
}
