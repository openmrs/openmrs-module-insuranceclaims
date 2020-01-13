<%
    ui.includeJavascript("uicommons", "angular.js")
    ui.includeJavascript("uicommons", "ngDialog/ngDialog.js")
    ui.includeCss("uicommons", "ngDialog/ngDialog.min.css")
    ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("Add new insurance claim") ])
    ui.includeJavascript("uicommons", "directives/select-concept-from-list.js")
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

<style>
    .selectedProvidedItem {background-color: MediumSeaGreen;}
    .unselectedProvidedItem {background-color: gray;}
</style>


<script>
    var providedItems = {};
    
    function showElement(itemName) {
        var detailsBlock = document.getElementById(itemName)
        if (detailsBlock.style.display == 'none') {
            detailsBlock.style.display = 'block';
        } else {
            detailsBlock.style.display = 'none';
        }
    }

    function changeColor(item, colorName) {
        item.style.backgroundColor = colorName;
    }

    function selectProvidedItem(itemType, item) {
        providedItems[itemType]['items'].push(item.id);
        item.classList.remove('unselectedProvidedItem');
        item.classList.add('selectedProvidedItem');
    }

    function unselectProvidedItem(itemType, item) {
        let indexOfItem = providedItems[itemType]['items'].indexOf(item.id);
        if (indexOfItem >= 0) {
        	providedItems[itemType]['items'].splice( indexOfItem, 1 );
        }
        item.classList.remove('selectedProvidedItem');
        item.classList.add('unselectedProvidedItem');
    }

    function changeProvidedItemSelection(itemType, item) {
        if (item.classList.contains('unselectedProvidedItem')) {
            selectProvidedItem(itemType, item);
        } else {
            unselectProvidedItem(itemType, item);
        }
    }

    function addDiagnosis(diagnosis) {
        this.selectedDiagnoses.add(diagnosis);
    }

    function removeDiagnosis(diagnosis) {
        this.selectedDiagnoses.remove(diagnosis);
    }

    function changeDiagnosisSelection(diagnosis) {
        let indexOfDiagnosis = selectedDiagnoses.indexOf(diagnosis);
        if (indexOfDiagnosis >= 0) {
            removeDiagnosis(diagnosis);
        } else {
            addDiagnosis(diagnosis);
        }
    }
</script>


<% if(result) {%>
 <div> Post result: ${result} </div>
<% } %>


<div id="new-insurance-claim-app" ng-controller="InsuranceClaimsCtrl" ng-init='init()'>
<form id="newInsuranceClaim" method="post" autocomplete="off">
    <fieldset>
    <% if(providedItems) {%>
        <br/>
        <tr>
            <% providedItems.eachWithIndex { item, index -> 
                def details = "Details"
                def itemDetailsId = item.key + details %>

                <tr> <div id="${item.key}" style="cursor: pointer;"> ${index + 1}. ${item.key} </div> </tr>
                <div id="${itemDetailsId}" style="display: none;">
                    <%item.value.eachWithIndex { providedItem, itemIndex -> %>
                        <tr>
                            <div class="consumedItemsOfType" id="${providedItem.uuid}" style="border: 2px solid black;">
                                ${itemIndex + 1}. ${providedItem.dateOfServed} 
                            </div>
                            <script>
                                providedItems['${item.key}'] = {};
                                providedItems['${item.key}']['items'] = [];
                                document.getElementById("${providedItem.uuid}").onclick = function () {
                                    changeProvidedItemSelection('${item.key}', this);
                                }
                            </script>
                        </tr>
                    <% } %>
                    <br>
                    <tr> Item explanation   : <input type="text" id="${item.key}Explanation" value="">   </tr> <br>
                    <tr> Item justification : <input type="text" id="${item.key}Justification" value=""> </tr> <br>
                    <script>
                        jQuery("[id= '${item.key}Explanation']").change( function() {
                                providedItems['${item.key}']['explanation'] = jQuery(this).val();
                            });
                        
                        jQuery("[id= '${item.key}Justification']").change(function() {
                                providedItems['${item.key}']['justification'] = jQuery(this).val();
                            });
                    </script>
                </div>

                <script>
                    document.getElementById('${item.key}').onclick = function () {
                        showElement('${itemDetailsId}');
                        let consumed = document.getElementById('${itemDetailsId}')
                                            .getElementsByClassName('consumedItemsOfType');
                        if (this.style.backgroundColor == 'lightgreen') {
                            changeColor(this, 'white');
                            for (var i = 0; i < consumed.length; i++) {
                                unselectProvidedItem('${item.key}', consumed[i]);
                            }  
                        } else {
                            changeColor(this, 'lightgreen');
                            for (var i = 0; i < consumed.length; i++) {
                                selectProvidedItem('${item.key}', consumed[i]);
                            }   
                        }
                    };
                </script>
                <br/>
            <% } %>
        </tr>
        <br />
        Claim explanation   : <input type="text" id="claimExplanation" value="">   </tr> <br>
        Claim justification : <input type="text" id="claimJustification" value=""> </tr> <br>
    <% } %>
    ${ ui.includeFragment("uicommons", "field/datetimepicker", [ id: 'startDate', label: 'Treatment start',
        formFieldName: 'startDate', useTime: '', ]) }
    ${ ui.includeFragment("uicommons", "field/datetimepicker", [ id: 'endDate', label: 'Treatment end: ',
        formFieldName: 'endDate', useTime: '', ]) }
    ${ ui.includeFragment("uicommons", "field/location", [ id: 'location', label: 'Facility: ',
        formFieldName: 'location', ]) }

    <tr> Claim code: <input type="text" id="claimCode" value="">   </tr> <br>
    <tr> Guarantee id: <input type="text" id="guaranteeId" value="">   </tr> <br>

    <% if(patientDiagnoses) {%>
    <div id="diagnoses"> Diagnoses: <br>
        <% patientDiagnoses.eachWithIndex { diagnosis, index ->  %>
            <script>console.log([[${diagnosis}]])</script>
            <span id="diagnosis${diagnosis.id}" class="diagnosis-span" value="${diagnosis.uuid}">
                <input type="checkbox" class="diagnosis-checkbox">${diagnosis.name}<br>
            </span>
        <% } %>
    </div>
        <script>
            var diagnoses = [];
            jQuery('.diagnosis-checkbox').change( function() {
                let box = jQuery(this);
                let thisDiagnosis = jQuery(this).parent().attr("value");
                if (box.prop("checked")) {
                    diagnoses.push(thisDiagnosis);
                } else {
                    let diagnosisIndex = diagnoses.indexOf(thisDiagnosis);
                    diagnoses.splice(diagnosisIndex, 1);
                }
                jQuery('#selectedDiagnosis').html(diagnoses);
            });
        </script>
    <% } %>
    <br>
    <div id="paymentOption"> 
        Paid in facility: <input id="paidInFacility" type="checkbox" ><br>
    </div>

    <% if(visitTypes) { %>
        <p>
        <label for="visitType-label">Visit type: </label>
        <select id="visitType">
         <% visitTypes.eachWithIndex { visit, index -> %>
             <option value="${visit.uuid}">${visit.name}</option>
        <% } %>
        </select>
        </p>
    <% } %>
    <button id="awesomeButton" type="button" onclick="showProvided()">Whats in provided items!</button> 
    <button id="addClaimButton" type="button" onclick="submitNewClaim()" > Send claim </button>
    </fieldset>

    <input type="hidden" name="redirectUrl" value="patientClaims" />
    <input type="hidden" name="providedItems" value=JSON.stringify(providedItems); />
    <input type="hidden" name="selectedDiagnosis" value=JSON.stringify(diagnosees); />
    <input type="hidden" id="provider" value="${providerUuid}" />
    <input type="hidden" id="storagePatientUuid" name="patientUuid" value="${patientUuid}" /> 
</form>
<div id="formData"></div>

<script>
 
    function showProvided() {
        document.getElementById("formData").innerHTML = JSON.stringify(getFormData());
    }
   
    function getFormData() {
        return {
            "providedItems": providedItems,
            "diagnoses": diagnoses,
            "claimExplanation": document.getElementById("claimExplanation").value,
            "claimJustification": document.getElementById("claimJustification").value,
            "startDate": document.getElementById("startDate-field").value,
            "endDate": document.getElementById("endDate-field").value,
            "location": document.getElementById("location-field").value,
            "paidInFacility": document.getElementById("paidInFacility").checked,
            "patient": document.getElementById("storagePatientUuid").value,
            "claimCode":  document.getElementById("claimCode").value,
            "guaranteeId":  document.getElementById("guaranteeId").value,
            "visitType": document.getElementById("visitType").value,
            "provider":  document.getElementById("provider").value
        }
    }

    function submitNewClaim() {

        var formData = JSON.stringify(getFormData());

        jQuery.ajax({
        type: "POST",
        data: formData,
        url: "../ws/insuranceclaims/rest/v1/claims",
        success: function(){alert("It worked!")},
        error: function (request, status, error) {
            document.getElementById("formData").innerHTML = request;
        },
        dataType: "json",
        contentType : "application/json"
        }).done(function(data) {
                // log data to the console so we can see
                console.log(data); 
                document.getElementById("formData").innerHTML = data;
                // here we will handle errors and validation messages
            });
    }
</script>
</div>