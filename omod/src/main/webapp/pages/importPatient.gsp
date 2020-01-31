<%
    ui.includeJavascript("uicommons", "angular.js")
    ui.includeJavascript("uicommons", "ngDialog/ngDialog.js")
    ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("Add new insurance claim") ])
%>

<% if (externalPatientId) { %>
    Result for patient with id ${externalPatientId}:
    <% if (valid) { %>
    <div style="border: solid 1px">
        <div style="border: solid 1px"> Name: ${patient.personName} </div>
        <% if (patient.identifiers) { %>
        <div style="border: solid 1px">
        Identifiers: 
            <%patient.identifiers.eachWithIndex { identifier, index ->  %> 
            <tr> ${index}. ${identifier} </tr>
            <% } %>
        <% } %>
        </div>
        <div style="border: solid 1px"> Birthdate: ${patient.birthdate} (Estimated? ${patient.birthdateEstimated}) </div>
        <div style="border: solid 1px"> Gender: ${patient.gender} </div>

        <div style="border: solid 1px"> 
        <form id="requestForm" method="POST" action="">
            <button id="savePatient" type="submit"> Save this patient </button>
            <input name="externalPatientId" type="hidden" value="${externalPatientId}" />
        </form> 
        </div>

    </div>
    <% }  else { if (created) {%>
        Patient was successfully created 
        <% } else {%>
        No patient with this id was found
        <% } %>
    <% } %>

<% } else { %>
    <form id="searchForPatient" method="GET" action="">
        Patient external id: 
        <input type="text" name="externalPatientId" id="externalPatientId" value="">   </tr> 
        <button id="getPatient" type="submit"> Check patient </button>
    </form>
<% } %>
