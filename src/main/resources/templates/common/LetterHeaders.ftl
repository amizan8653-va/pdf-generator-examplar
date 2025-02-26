<#macro recipientFullName>
    <#compress>
        ${firstName?capitalize?html} ${middleName?capitalize?html} <#list lastName?split("-") as x>${x?trim?capitalize?html}<#if x_has_next>-</#if></#list> ${suffixName?html}
    </#compress>
</#macro>
<#macro recipientFullNameNoSuffix>
    <#compress>
        ${firstName?capitalize?html} ${middleName?capitalize?html} <#list lastName?split("-") as x>${x?trim?capitalize?html}<#if x_has_next>-</#if></#list>
    </#compress>
</#macro>
<#macro recipientSalutation>
    <#compress>
        ${salutationName?capitalize?html} <#list lastName?trim?split("-") as x>${x?trim?capitalize?html}<#if x_has_next>-</#if></#list>
    </#compress>
</#macro>
<#macro headOfFamilyFullName>
    <#compress>
        ${headOfFamilyFirstName?capitalize?html} <#list headOfFamilyLastName?split("-") as x>${x?trim?capitalize?html}<#if x_has_next>-</#if></#list>
    </#compress>
</#macro>

<#macro recipientAddress>
    <#if country?? && country?lower_case != "united states">
        <#assign addressType = "International">
    </#if>
    <#if state?? && (state?lower_case == "ap" || state?lower_case == "ae" || state?lower_case == "aa")>
        <#assign addressType = "Overseas Military">
    </#if>
    <#if country?? && country?lower_case == "united states">
        <#assign addressType = "Domestic">
    </#if>


    <#assign addressLineRowsNo = 0>

    <#if addressLine1?? && addressLine1?length &gt; 0>
        <#assign addressLineRowsNo = addressLineRowsNo + 1>
        ${addressLine1?capitalize?html}  <br/>
    </#if>
    <#if addressLine2?? && addressLine2?length &gt; 0>
        <#assign addressLineRowsNo = addressLineRowsNo + 1>
        ${addressLine2?capitalize?html}<br/>
    </#if>
    <#if addressLine3?? && addressLine3?length &gt; 0>
        <#assign addressLineRowsNo = addressLineRowsNo + 1>
        ${addressLine3?capitalize?html}  <br/>
    </#if>

    <#if addressType == "Domestic">
        ${city?capitalize?html}, ${state?html} ${zipCode?html}<br/>
    <#elseif addressType == "International">
        ${city?capitalize?html}, ${country?upper_case?html}<br/>
    <#elseif addressType == "Overseas Military">
        ${city?upper_case?html} ${state?html} ${zipCode?html}<br/>
    </#if>

    <#if addressLine3?length &lt; 1>
        <#assign addressLineRowsNo = addressLineRowsNo + 1>
        <br/>
    </#if>

    <#assign addressLineRowsNo = addressLineRowsNo + 1>
</#macro>
