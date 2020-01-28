<%
    ui.includeJavascript("uicommons", "angular.js")
    ui.includeJavascript("uicommons", "ngDialog/ngDialog.js")
    ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("Insurance claims") ])
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
<b><font size="16">${ patientName }</font></b>
<% def link="singleClaimView.page?patientId=" + patientId %>
<button style="float: right;" onclick="location.href='${link}'" type="button">Create claim</button>
</span>

<br><br>

<table>
  <tr>
   <th>Claim code</th>
   <th>Expire</th>
   <th>Status</th>
   <th width="145px">Action</th>
   <th width="95px">Details</th>
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
        <td align="center">
            <% def linkDetails="singleClaimView.page?patientId=" + patientId + "&claimUuid=" + it.uuid %>
            <button style="width:95px;" onclick="location.href='${linkDetails}'" type="button">Details</button>
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
        dataType: "json",
        contentType : "application/json",
        complete: function() {
            reloadPage();
        }});
    }

    function updateClaim(uuid) {
        jQuery.ajax({
        type: "GET",
        url: "../ws/insuranceclaims/rest/v1/claims/updateClaim?claimUuid=" + uuid,
        dataType: "json",
        contentType : "application/json",
        complete: function() {
            reloadPage();
        }});
    }

    function reloadPage(){
        setTimeout(function(){
            window.location.reload();
        }, 2000);
    }
    
</script>