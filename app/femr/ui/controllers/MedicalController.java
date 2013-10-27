package femr.ui.controllers;

import com.google.inject.Inject;
import com.google.inject.Provider;
import femr.business.dtos.CurrentUser;
import femr.business.dtos.ServiceResponse;
import femr.business.services.ISearchService;
import femr.business.services.ISessionService;
import femr.business.services.ITriageService;
import femr.common.models.IPatient;
import femr.common.models.IPatientEncounter;
import femr.common.models.IPatientEncounterVital;
import play.mvc.Controller;
import play.mvc.Result;
import femr.ui.views.html.medical.index;
import femr.ui.views.html.medical.indexPopulated;
import femr.ui.models.medical.CreateViewModel;

public class MedicalController extends Controller {

    private ISessionService sessionService;
    private ISearchService searchService;
    private ITriageService triageService;
    private IPatientEncounterVital patientEncounterVital;


    @Inject
    public MedicalController(ISessionService sessionService,
                             ISearchService searchService) {
        this.sessionService = sessionService;
        this.searchService = searchService;

    }

    public Result createGet() {

        boolean error = false;

        CurrentUser currentUserSession = sessionService.getCurrentUserSession();
        return ok(index.render(currentUserSession,error));
    }

    public Result createPopulatedGet(){
        boolean error = false;

        CurrentUser currentUserSession = sessionService.getCurrentUserSession();

        String s_patientID = request().getQueryString("id");
        int i_patientID = Integer.parseInt(s_patientID);

        ServiceResponse<IPatient> patientServiceResponse = searchService.findPatientById(i_patientID);
        if (patientServiceResponse.hasErrors()){
            error = true;
            return ok(index.render(currentUserSession,error));
        }

        ServiceResponse<IPatientEncounter> patientEncounterServiceResponse =
                searchService.findCurrentEncounterByPatientId(i_patientID);
        if (patientEncounterServiceResponse.hasErrors()){
            error = true;
            return ok(index.render(currentUserSession,error));
        }

        IPatient patient = patientServiceResponse.getResponseObject();
        IPatientEncounter patientEncounter = patientEncounterServiceResponse.getResponseObject();

        return ok(indexPopulated.render(currentUserSession,patient,patientEncounter));
    }
}