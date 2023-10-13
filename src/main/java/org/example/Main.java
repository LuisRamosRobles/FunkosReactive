package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.Funko;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello world!");

        String archivoCsv = System.getProperty("user.dir") + "/data/funkos.csv";

        try(BufferedReader lector = new BufferedReader(new FileReader(archivoCsv))){
            Flux<Funko> funkoFlux = Flux.using(
                    () -> lector.lines().skip(1),
                    Flux::fromStream,
                    Stream::close
            )
                    .map(linea -> linea.split(","))
                    .map(valores -> Funko.builder()
                            .cod(UUID.fromString(valores[0].substring(0, 35)))
                            .nombre(valores[1])
                            .modelo(valores[2])
                            .precio(Double.parseDouble(valores[3]))
                            .fechaLanzamiento(LocalDate.parse(valores[4]))
                            .build()
                    )
                    .subscribeOn(Schedulers.parallel());

            funkoFlux.subscribe(System.out::println);



        }catch(IOException e){
            System.out.println("Error en la lectura del archivo: " + e.getMessage());
        }

    }
}