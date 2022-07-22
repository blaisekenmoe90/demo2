package controller;

import model.Articolo;
import model.Ordine;
import model.TariffaCorriere;
import model.payload.OrdineConPreventivo;
import repository.ArticoliRepository;
import repository.OrdiniRepository;
import repository.TariffeRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@CrossOrigin("*")
@RestController
public class Controller {

    //-----------------------------------get semplici-------------------------------
    @GetMapping("/articoli")
    public static ArrayList<Articolo> getAllArticoli() {
        return ArticoliRepository.getAllArticoli();
    }

    @GetMapping("/tariffe")
    public static ArrayList<TariffaCorriere> getAllTariffe() {
        return TariffeRepository.getAllTariffe();
    }

    @GetMapping("/ordini")
    public static ArrayList<Ordine> getAllOrdini() {
        return OrdiniRepository.getAllOrdini();
    }

    //-------------------------------------gia filtrate---------------------------

    @GetMapping("/ordinConPreventivo")
    public static ArrayList<OrdineConPreventivo> getAllOrderWithTheirBetterRate() {
        ArrayList elencoOrdiniConPreventivo = new ArrayList();
        //prendo tutti gli ordini
        ArrayList<Ordine> ordini = OrdiniRepository.getAllOrdini();
        //per ogni ordine mi trovo la tariffa migliore
        for (Ordine ordine: ordini) {
            //mi cerco il preventivo migliore
            TariffaCorriere migliorPreventivoPerQuestordine = getBetterRaceOfThisOrder(ordine);
            //creo l'oggetta aggiungendoci il preventivo
            OrdineConPreventivo ordineConPreventivo = new OrdineConPreventivo(
                    ordine.getId(),
                    ordine.getNumero(),
                    ordine.getData(),
                    migliorPreventivoPerQuestordine);
            //e lo aggiungo all'array
            elencoOrdiniConPreventivo.add(ordineConPreventivo);
        }
        return  elencoOrdiniConPreventivo;
    }

    //----------------------metodi per filtrare il tutto-------

    //1 richiamato
    public static TariffaCorriere getBetterRaceOfThisOrder(Ordine ordine) {
        //avendo l'id dell'ordine mi recupero tutti gli articoli associati
        ArrayList<Articolo> articoliDiQuestoOrdine = ArticoliRepository.getAllArticoliByOrder(ordine.getId());
        //una volta ottenuti tutti gli articoli mi calcolo il totale del peso dell'ordina
        double totalePesoOrdine = summWeightsOfArticles(articoliDiQuestoOrdine);
        //mi controllo fra tutti qual'è il preventivo migliore per questo ordine
        return searchBetterRaceOfThisOrder(totalePesoOrdine);
    }


    //2 richiamato
    public static double summWeightsOfArticles(ArrayList<Articolo> articles) {
        double sumOfAllArticles = 0;
        for (Articolo weightArticles: articles) {
            sumOfAllArticles = sumOfAllArticles + weightArticles.getPeso();
        }
        return sumOfAllArticles;
    }


    // 3 richiamato
    public static TariffaCorriere searchBetterRaceOfThisOrder(double pesoTotaleOrdine) {

        ArrayList<TariffaCorriere> tutteLeTariffe = TariffeRepository.getAllTariffe();
        ArrayList<TariffaCorriere> tutteLeTariffeValide = new ArrayList<>();

        //mi recupero tutte le tariffe valide (cioè che il peso ci sta)
        for (TariffaCorriere rate: tutteLeTariffe) {
            if(pesoTotaleOrdine < rate.getPeso_max()){
                tutteLeTariffeValide.add(rate);
            }
        }
        //guardo tra tutte quelle valide quella con costo minore
        TariffaCorriere betterRate = tutteLeTariffeValide.get(0);
        for (TariffaCorriere rate: tutteLeTariffeValide) {
            if(betterRate.getCosto() > rate.getCosto() ) {
                betterRate = rate;
            }
        }
        return betterRate;
    }

}