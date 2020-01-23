<%
    if (config.classes && config.classes.contains("regex")) {
        config.require("regex")
    }
    ui.includeJavascript("insuranceclaims", "insuranceValidation.js")
    ui.includeCss("insuranceclaims", "insuranceValidation.css")
%>


<div class="info-section">
    <% if (widgetMode) { %>
        <div class="info-header">
            <i class="icon-user"></i>
            <h3>${ui.message("insuranceclaims.policy.eligibility.label")}</h3>
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
                    value="${ config.initialValue ?: '' }"
                    <% if (config.classes) { %>class="${ config.classes.join(' ') }" <% } %>
                    <% if (config.size) { %> size="${ config.size }" <% } %>
                    <% if (config.maxLength) { %> maxlength="${ config.maxLength }" <% } %>
                    <% if (config.classes && config.classes.contains("regex")) { %> regex="${ config.regex }" <% } %> />
            </p>
            <span id="${ config.id }-field-empty" class="field-error" style="display: none;">
                ${ ui.message("insuranceclaims.required") }
            </span>
            <div>
                <button id="insurance-validation-submit" class="confirm" onClick="insuranceValidator.submit('${ config.id }'); return false;">
                    ${ ui.message("insuranceclaims.policy.eligibility.button") }
                    <i id="${ config.id }-icon" class="icon-spinner icon-spin icon-2x" style="display: none; margin-left: 10px;"></i>
                </button>
                <div id="${ config.id }-validation-result"></div>
            </div>
        </div>
    </div>
</div>
