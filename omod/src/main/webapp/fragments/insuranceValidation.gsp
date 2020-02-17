<%
    if (config.classes && config.classes.contains("regex")) {
        config.require("regex")
    }
    ui.includeJavascript("insuranceclaims", "insuranceValidation.js")
    ui.includeCss("insuranceclaims", "insuranceValidation.css")
%>

<script type="text/javascript">
    jq(document).ready(function() {
        var table = jq('#${ config.id }-validation-result');
        var url = '/' + OPENMRS_CONTEXT_PATH + '/insuranceclaims/insuranceValidation/actualPolices.action';
        jq.ajax({
            url: url,
            type: 'GET',
            dataType: "json",
            data: { personUuid: '${ personUuid }'},
            success: function(data) {
                if (data && data.results && data.results.length) {
                    insuranceValidator.renderPolicyResults('${ config.id }', data.results);
                    table.show();
                }
            },
            error: function(xhr, status, error) {
                table.hide();
            }
        });
    });
</script>

<div class="info-section">
    <% if (widgetMode) { %>
        <div class="info-header">
            <i class="icon-user"></i>
            <h3>${ ui.message("insuranceclaims.policy.eligibility.label") }</h3>
        </div>
    <% } %>

    <div class="info-body">
        <div class="insurance-validation-container">
            <p id="insurance-validation-question" <% if (config.left) { %> class="left" <% } %> >
                <label for="${ config.id }-field" id="insurance-validation-label">
                    ${ ui.message("insuranceclaims.policy.number.label") }
                </label>
                ${ ui.includeFragment("uicommons", "fieldErrors", [ fieldName: "policyNumber" ]) }
                <input
                    type="text"
                    id="${ config.id }-field"
                    name="policyNumber"
                    value=""
                    <% if (config.classes) { %>class="${ config.classes.join(' ') }" <% } %>
                    <% if (config.size) { %> size="${ config.size }" <% } %>
                    <% if (config.maxLength) { %> maxlength="${ config.maxLength }" <% } %>
                    <% if (config.classes && config.classes.contains("regex")) { %> regex="${ config.regex }" <% } %> />
            </p>
            <span id="${ config.id }-field-empty" class="field-error" style="display: none;">
                ${ ui.message("insuranceclaims.required") }
            </span>
            <div>
                <button
                    id="insurance-validation-submit" class="confirm"
                    onClick="insuranceValidator.submit('${ config.id }', '${ personUuid }'); return false;" >
                    ${ ui.message("insuranceclaims.policy.eligibility.button") }
                    <i id="${ config.id }-icon" class="icon-spinner icon-spin icon-2x" style="display: none; margin-left: 10px;"></i>
                </button>
                <div
                    id="${ config.id }-validation-result"
                    class="insurance-validation-result"
                    style="display: none;">
                    <table>
                        <thead>
                            <tr>
                                <th>${ ui.message("insuranceclaims.policy.number") }</th>
                                <th>${ ui.message("insuranceclaims.policy.status") }</th>
                                <th>${ ui.message("insuranceclaims.policy.expiryDate") }</th>
                                <th>${ ui.message("insuranceclaims.policy.allowedMoney") }</th>
                            </tr>
                        </thead>
                        <tbody id="${ config.id }-validation-result-body">
                        </tbody>
                    </table>
                    <div id="${ config.id }-all-covered" >   
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
