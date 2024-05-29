package shx.cotacaodolar.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import shx.cotacaodolar.model.Moeda;
import shx.cotacaodolar.model.Periodo;



@Service
public class MoedaService {

    // o formato da data que o método recebe é "MM-dd-yyyy"
    public List<Moeda> getCotacoesPeriodo(String startDate, String endDate) throws IOException, MalformedURLException, ParseException{
        Periodo periodo = new Periodo(startDate, endDate);

        String urlString = "https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata/CotacaoDolarPeriodo(dataInicial=@dataInicial,dataFinalCotacao=@dataFinalCotacao)?%40dataInicial='" + periodo.getDataInicial() + "'&%40dataFinalCotacao='" + periodo.getDataFinal() + "'&%24format=json&%24skip=0&%24top=" + periodo.getDiasEntreAsDatasMaisUm();

        URL url = new URL(urlString);
        HttpURLConnection request = (HttpURLConnection)url.openConnection();
        request.connect();

        JsonElement response = JsonParser.parseReader(new InputStreamReader((InputStream)request.getContent()));
        JsonObject rootObj = response.getAsJsonObject();
        JsonArray cotacoesArray = rootObj.getAsJsonArray("value");

        List<Moeda> moedasLista = new ArrayList<Moeda>();

        for(JsonElement obj : cotacoesArray){
            Moeda moedaRef = new Moeda();
            Date data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(obj.getAsJsonObject().get("dataHoraCotacao").getAsString());

            moedaRef.preco = obj.getAsJsonObject().get("cotacaoCompra").getAsDouble();
            moedaRef.data = new SimpleDateFormat("dd/MM/yyyy").format(data);
            moedaRef.hora = new SimpleDateFormat("HH:mm:ss").format(data);
            moedasLista.add(moedaRef);
        }
        return moedasLista;
    }


    public double getCurrentQuote  ()throws IOException, MalformedURLException, ParseException {
        Date currentDate = new Date();

        // Inicia uma instânica da classe Calendar
        Calendar calendar = Calendar.getInstance();

        // Atribui a data desejada a instânica
        calendar.setTime(currentDate);

        int i = 0;
        double cota = 0.0;
        while(i == 0){
            Date startDate = calendar.getTime();

            // Formatar as duas datas
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
            String currentDateFormatted = dateFormat.format(currentDate);
            String yesterdayDateFormatted = dateFormat.format(startDate);
            List<Moeda> cotacao = getCotacoesPeriodo(yesterdayDateFormatted, currentDateFormatted);

            if(!cotacao.isEmpty()){
                cota = cotacao.get(0).preco;
                i = 1;
            }else {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
            }
        }
        return  cota;
    }

    public List<Moeda> getQuoteLowerThanCurrent(String startDate, String endDate) throws IOException, MalformedURLException, ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Date startDateConvert = simpleDateFormat.parse(startDate);
        Date endDateConvert = simpleDateFormat.parse(endDate);

        if(endDateConvert.getTime() >= startDateConvert.getTime()){
            Double currentPrice = getCurrentQuote();
            List<Moeda> quotes = getCotacoesPeriodo(startDate, endDate);
            List<Moeda> quotesLowerThanCurrent = new ArrayList<>();

            for(Moeda price: quotes){
                if(price.preco < currentPrice){
                    quotesLowerThanCurrent.add(price);
                }
            }
            return quotesLowerThanCurrent;
        }else {
            throw  new IllegalArgumentException("A data inicial não pode ser menor que a final");
        }
    }
}
