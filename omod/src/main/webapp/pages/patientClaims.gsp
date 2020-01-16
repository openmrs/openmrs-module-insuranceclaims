<%
    ui.includeJavascript("uicommons", "angular.js")
    ui.includeJavascript("uicommons", "ngDialog/ngDialog.js")
    ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("Add new insurance claim") ])
%>

<openmrs:htmlInclude file="/moduleResources/htmlformentry/htmlForm.js" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js"/>
<openmrs:htmlInclude file="/scripts/jquery-ui/js/openmrsSearch.js" />

<openmrs:htmlInclude file="/dwr/engine.js" />
<openmrs:htmlInclude file="/dwr/util.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRHtmlFormEntryService.js" />
<openmrs:htmlInclude file="/moduleResources/htmlformentry/htmlFormEntry.js" />
<openmrs:htmlInclude file="/moduleResources/htmlformentry/htmlFormEntry.css" />
<openmrs:htmlInclude file="/moduleResources/htmlformentry/jquery-ui-1.8.17.custom.css" />


<span>
<b>${ patientName }</b>
<% def link="addClaim.page?patientId=" + patientId %>
<button style="float: right;" onclick="location.href='${link}'" type="button">Create claim</button>
</span>

<br><br>
<span>List of Claims</span>
<br>

<table>
  <tr>
   <th>Claim code</th>
   <th>Expire</th>
   <th>Status</th>
   <th>Option</th>
  </tr>
  <% if (patientClaims) { %>
     <% patientClaims.each { %>
      <tr>
        <td>${ ui.format(it.claimCode) }</td>
        <td>${ ui.format(it.dateTo) }</td>
        <td>${ ui.format(it.status) }</td>
        <td align="center">
            <% if (it.externalId) { %>
                <button style="width:145px;" type="button" onclick="updateClaim('${it.uuid}')">Update claim</button>
            <% } else { %>
                <button style="width:145px;" type="button" onclick="sendClaim('${it.uuid}')">Send claim</button>
            <% } %>
        </td>
      </tr>
    <% } %>
  <% } else { %>
  <tr>
    <td colspan="2">${ ui.message("None of insurance claims") }</td>
  </tr>
  <% } %>
</table>

<script>
    function sendClaim(uuid) {
        jQuery.ajax({
        type: "GET",
        url: "../ws/insuranceclaims/rest/v1/claims/sendToExternal?claimUuid=" + uuid,
        success: function(){
            console.log('Success');
            setTimeout(function(){
                window.location.reload();
                console.log('Reloaded');
            }, 3000);
        },
        error: function (request, status, error) {
            console.log(JSON.stringify(request) + JSON.stringify(status) + JSON.stringify(error));
            setTimeout(function(){
                window.location.reload();
                console.log('Reloaded');
            }, 3000);
        },
        dataType: "json",
        contentType : "application/json"
        });
    }

    function updateClaim(uuid) {
        jQuery.ajax({
        type: "GET",
        url: "../ws/insuranceclaims/rest/v1/claims/updateClaim?claimUuid=" + uuid,
        success: function(){
            console.log('Success');
        },
        error: function (request, status, error) {
            console.log(JSON.stringify(request) + JSON.stringify(status) + JSON.stringify(error));
        },
        dataType: "json",
        contentType : "application/json"
        });
    }
</script>