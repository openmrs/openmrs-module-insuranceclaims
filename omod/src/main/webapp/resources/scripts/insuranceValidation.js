var insuranceValidator = insuranceValidator || {};

insuranceValidator.submit = function(inputId) {
    jq('#' + inputId + '-field-empty').hide();
    emr.loadMessages([
        "insuranceclaims.success",
        "insuranceclaims.failed"
    ]);
    var policyNumber = document.getElementById(inputId + '-field').value;

    if (!policyNumber) {
        jq('#' + inputId + '-field-empty').css({'color' : 'red', display : 'inline'}).show();
    } else {
        jq('#' + inputId + '-icon').css('display', 'inline-block').parent().addClass('disabled');
        var url = '/' + OPENMRS_CONTEXT_PATH + '/ws/insuranceclaims/rest/v1/claims/getPolicyFromExternal';
        jq.ajax({
            url: url,
            type: 'GET',
            data: { policyNumber: policyNumber},
            success: function() {
                emr.successMessage('insuranceclaims.success');
            },
            error: function() {
                emr.errorMessage("insuranceclaims.failed");
            },
            complete: function() {
                jq('#' + inputId + '-icon').css('display', 'none').parent().removeClass('disabled');
            }
        });
    }
}
