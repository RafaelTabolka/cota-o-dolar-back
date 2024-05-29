package shx.cotacaodolar.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import shx.cotacaodolar.model.Moeda;
import shx.cotacaodolar.service.MoedaService;


@RestController
@RequestMapping(value = "/")
public class MoedaController {

    @Autowired
    private MoedaService moedaService;

    @GetMapping("/moeda/{data1}&{data2}")
    public List<Moeda> getCotacoesPeriodo(@PathVariable("data1") String startDate,
                                          @PathVariable("data2") String endDate)
            throws IOException, MalformedURLException, ParseException{
        return moedaService.getCotacoesPeriodo(startDate, endDate);
    }

    // Desenvolver uma rotina que retorna a cotação atual
    @GetMapping("/moeda/atual")
    public double getCotacaoAtual() throws IOException, MalformedURLException, ParseException{
        return moedaService.getCurrentQuote();
    }

    // Desenvolver uma rotina que recebe um período e retorna uma lista de cotações, somente com as cotações menores que a cotação atual.
    @GetMapping("/moeda/menorAtual/{data1}&{data2}")
    public List<Moeda> getCotacoesMenoresAtual(@PathVariable("data1") String startDate, @PathVariable("data2") String endDate) throws IOException, MalformedURLException, ParseException{
        return moedaService.getQuoteLowerThanCurrent(startDate, endDate);
    }

}
