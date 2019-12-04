<% ui.decorateWith("appui", "standardEmrPage") %>

Hello, world.

<% if (context.authenticated) { %>
    This is the most recent version! Even more new!
    And a special hello to you, $context.authenticatedUser.personName.fullName.
    Your roles are:
    <% context.authenticatedUser.roles.findAll { !it.retired }.each { %>
        $it.role ($it.description)
    <% } %>
<% } else { %>
    You are not logged in.
<% } %>

${ ui.includeFragment("insuranceclaims", "insuranceClaimsToSend") }

<form:form method="POST"
          action="/module/insuranceclaims/insuranceclaims.form" modelAttribute="employee">
             <table>
                <tr>
                    <td><form:label path="name">Name</form:label></td>
                    <td><form:input path="name"/></td>
                </tr>
                <tr>
                    <td><form:label path="id">Id</form:label></td>
                    <td><form:input path="id"/></td>
                </tr>
                <tr>
                    <td><form:label path="contactNumber">
                      Contact Number</form:label></td>
                    <td><form:input path="contactNumber"/></td>
                </tr>
                <tr>
                    <td><input type="submit" value="Submit"/></td>
                </tr>
            </table>
        </form:form>
<br/><br/>

<div id="output" style="margin-left: 20px;">
</div>
<br/>
<br/>


${ ui.includeFragment("insuranceclaims", "users") }
