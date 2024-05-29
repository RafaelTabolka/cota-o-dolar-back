package shx.cotacaodolar.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Periodo {
    private Date dataInicial;
    private Date dataFinal;
    private Long diasEntreAsDatas;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy"); 

    public Periodo(String dataInicial, String dataFinal) throws ParseException{
        this.dataInicial = new SimpleDateFormat("MM-dd-yyyy").parse(dataInicial);
        this.dataFinal = new SimpleDateFormat("MM-dd-yyyy").parse(dataFinal);
        diasEntreAsDatas = this.dataFinal.getTime()- this.dataInicial.getTime();
    }

    public String getDiasEntreAsDatasMaisUm(){
        Long resp = diasEntreAsDatas;
        resp =  TimeUnit.DAYS.convert(resp,TimeUnit.MILLISECONDS) + 1;
        return resp.toString();
    }

    public String getDataInicial(){
        return this.dateFormat.format(this.dataInicial);
    }

    public String getDataFinal(){
        return this.dateFormat.format(this.dataFinal);
    }

}
