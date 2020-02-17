var insuranceValidator = insuranceValidator || {};

insuranceValidator.submit = function(inputId, personUuid) {
    jq('#' + inputId + '-field-empty-body').hide();
    jq('#' + inputId + '-field-empty').hide();
    emr.loadMessages([
        "insuranceclaims.success",
        "insuranceclaims.failed"
    ]);
    var policyNumber = document.getElementById(inputId + '-field').value;
    var table = jq('#' + inputId + '-validation-result');

    if (!policyNumber) {
        jq('#' + inputId + '-field-empty').css({display : 'inline'}).show();
    } else {
        jq('#' + inputId + '-icon').css('display', 'inline-block').parent().addClass('disabled');
        var url = '/' + OPENMRS_CONTEXT_PATH + '/insuranceclaims/insuranceValidation/verify.action';
        jq.ajax({
            url: url,
            type: 'POST',
            dataType: "json",
            data: { policyNumber: policyNumber, personUuid: personUuid},
            success: function(data) {
                if (data && data.results && data.results.length) {
                    emr.successMessage('insuranceclaims.success');
                    insuranceValidator.renderPolicyResults(inputId, data.results);
                    table.show();
                } else {
                    emr.errorMessage("insuranceclaims.failed");
                }
                insuranceValidator.renderCoveredPatients(inputId, data.coveredByPolicy)
                console.log(data)
            },
            error: function(xhr, status, error) {
                emr.errorMessage("insuranceclaims.failed");
                table.hide();
            },
            complete: function() {
                jq('#' + inputId + '-icon').css('display', 'none').parent().removeClass('disabled');
            }
        });
    }
}

insuranceValidator.renderPolicyResults = function(inputId, data) {
    var tableBody = jq('#' + inputId + '-validation-result-body');
    tableBody.html('')
    jq.each(data, function(rowIndex, r) {
        var row = jq("<tr/>");
        row.append(jq("<td/>").text(r.policyNumber))
            .append(jq("<td/>").text(r.status))
            .append(jq("<td/>").text(new Date(r.expiryDate).toLocaleDateString()))
            .append(jq("<td/>").text(r.allowedMoney));
        tableBody.append(row);
    });
}

insuranceValidator.renderCoveredPatients = function(inputId, covered) {
    var coveredDiv = jq('#' + inputId + '-all-covered');
    var result = covered.join(", ");
    coveredDiv.text("Covered: " + covered);
}
