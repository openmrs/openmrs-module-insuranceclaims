<%
    ui.includeJavascript("uicommons", "angular.js")
    ui.includeJavascript("uicommons", "ngDialog/ngDialog.js")
    ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("Bills") ])
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
</span>

<br><br>

<table>
  <tr>
   <th>Claimed amount</th>
   <th>Date created</th>
   <th>Start date</th>
   <th>End date</th>
   <th>Payment Status</th>
   <th>Payment Type</th>
  </tr>
  <% if (bills) { %>
     <% bills.each { %>
      <tr>
        <td>${ ui.format(it.totalAmount) }</td>
        <td>${ ui.format(it.dateCreated) }</td>
        <td>${ ui.format(it.startDate) }</td>
        <td>${ ui.format(it.endDate) }</td>
        <td>${ ui.format(it.paymentStatus) }</td>
        <td>${ ui.format(it.paymentType) }</td>
      </tr>
    <% } %>
  <% } else { %>
  <tr>
    <td colspan="2">${ ui.message("None of bills") }</td>
  </tr>
  <% } %>
</table>
