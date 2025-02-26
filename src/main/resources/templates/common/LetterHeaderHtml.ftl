<div id="header_container" style="width:500px; height:92px">
    <img src="common/va_seal.jpg" alt="Veteran Affairs Seal" width="92"
                                   height="92"/>
    <h1 style="position: absolute; left: 300px;padding-top: 40px;" class="headerAddress">DEPARTMENT OF VETERANS AFFAIRS</h1>
</div>
<br/>

<p>${letterDate?html}</p>
<p class="recipientAddress">
    <#include "LetterHeaders.ftl" />
    <@recipientFullName/><br/>
    <@recipientAddress/>
</p>
<br/>
