<!-- THIS LETTER IS CURRENTLY NOT SUPPORTED. -->
<!-- LEAVING THIS FTL FILE  IN PLACE IN THE EVENT THAT WE DO NEED TO SUPPORT -->
<!-- If we do end up needing to implement this letter, this can serve as a reference -->
<!DOCTYPE html>
<html lang="en">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Post 911 GI Bill Education Approval Letter</title>
    <link rel="stylesheet" href="common/portlet.css" type="text/css" media="screen, projection, print"/>
</head>

<body id="generated_letter" style="width:100%;font-size:75%">
<h1 class="SR_only" style="color:#ffffff;font-size:1px">
    Certificate of Eligibility Letter
</h1>

<#if fileNumber?? && fileNumber?length &gt; 0>
    <#if fileNumber?length = 9>
        <#assign fileNumber = "xxx-xx-" +  "${fileNumber?substring(5,9)?html}">
    <#elseif (fileNumber?length >= 8) >
        <#assign fileNumber = "${fileNumber?substring(0, 2)?html}-${fileNumber?substring(2, 5)?html}-${fileNumber?substring(5, 8)?html}">
    <#elseif (fileNumber?length < 8) >
        <#assign fileNumber = "${fileNumber?left_pad(8, '0')?html}">
        <#assign fileNumber = "${fileNumber?substring(0, 2)?html}-${fileNumber?substring(2, 5)?html}-${fileNumber?substring(5, 8)?html}">
    </#if>
<#elseif ssn?? && ssn?length &gt; 0>
    <#assign fileNumber = "xxx-xx-" + "${ssn?substring(5,9)?html}">
<#else>
    <#assign fileNumber = "--">
</#if>

<div id="header_container">
    <table class="tg">
        <tbody>
        <tr>
            <th/>
            <th/>
        </tr>
        <tr>
            <td style="padding-left:0px;"><img src="common/va_seal.jpg" alt="Veteran Affairs Logo" width="92"
                                               height="92"/></td>
            <td style="padding-left:70px;">
                <p class="headerAddressDocCentered" style="color:black;font-weight:normal">
                    DEPARTMENT OF VETERANS AFFAIRS<br/>
                    BUFFALO REGIONAL OFFICE <br/>
                    P.O. BOX 4616 <br/>
                    BUFFALO NY 14240-4616
                </p>
            </td>
        </tr>
        </tbody>
    </table>
</div>

<table class="tg">
    <tbody>
    <tr>
        <th/>
        <th/>
    </tr>
    <tr>
        <td colspan="2" style="padding-left:0px;">${letterDate?html}</td>
    </tr>
    <tr style="vertical-align:top">
        <td style="padding-left:0px;">
            <p class="recipientAddress">
                <#include "common/LetterHeaders.ftl" />
                <@recipientFullName/><br/>
                <@recipientAddress/>
            </p>
        </td>
        <td style="padding-left:350px;">
            <p class="replyRefer">
                ${firstInitialLastName}<br/>
                ${fileNumber?html}<br/>
            </p>
        </td>
    </tr>
    </tbody>
</table>

<p class="salutation" style="padding-bottom:10px;">Dear <@recipientSalutation/>:</p>
<div id="letter_container" style="margin:10px 0;">
    <h2 style="margin:0 0 5px 0;">Certificate of Eligibility</h2>
    <div>
        <p>
            This certifies that you are entitled to benefits for an approved program of education or training under the
            Post-9/11 GI Bill.
        </p>
        <p style="margin:5px auto">
        <table class="tg" width="100%" style="border:1px solid;">
            <tr>
                <td style="font-size:75%;font-weight:bold;text-align:center;">
                    You must take this letter to your school. Your school must certify your enrollment before you can
                    get paid.
                </td>
            </tr>
        </table>
        </p>
        <p style="margin:5px auto">
            You have ${enrollmentMonths} months and ${enrollmentDays} days of full-time benefits
            remaining.
        </p>
        <#--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
            TODO: need to include if-else to check if the veteran is on active duty. My guess is that the BirlsRecord must include at least one service with RELEASEDACTIVEDUTYDATE == null, but I need to confirm
        <p style="margin:5px auto">
            (Because you are on active duty, you currently have no delimiting date.)
        </p>
        --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------->
        <p style="margin:5px auto">
            You have until ${delimitingDate?datetime?string("MMM d, yyyy")} to use your benefits under this
            program, which is fifteen years from your last separation from active duty.
        </p>
        <p style="margin:5px auto">
            You're entitled to receive ${recipientPercentEntitlement}% of the benefits payable under the
            Post-9/11 GI Bill program for training offered by an institution of higher education. We determined this
            percentage based on your length of creditable active duty service.
        </p>
    </div>

    <h2 style="margin:25px 0 5px 0;">Yellow Ribbon</h2>
    <div>
        <p>
            Because you are eligible at the 100% benefit rate, you may also be eligible to participate in the Yellow
            Ribbon Program. The Yellow Ribbon Program allows schools to enter into an agreement with VA to provide
            additional financial assistance to individuals who are charged tuitions and fees that exceed the in-state
            maximum amount payable under the Post-9/11 GI Bill program. This benefit is only payable if the Post-9/11 GI
            Bill tuition and fee payment does not cover the full cost of your school's tuition and fees.
        </p>
        <p style="margin:10px auto">
            <strong>Note:</strong> <i>Individuals on active duty are not eligible for this program. The Yellow Ribbon
                Program is not available at all schools. To determine if your school participates or to get a list of
                Yellow Ribbon Program participating schools please visit <a href="http://www.GIBILL.va.gov"
                                                                            title="click to go to www.GIBILL.va.gov"
                                                                            target="_blank">www.GIBILL.va.gov</a>.</i>
        </p>
    </div>

    <h2 style="margin:25px 0 5px 0;">What You Must Do</h2>
    <div>
        <p>
            You should take this letter to your school's veterans certifying official as proof of your eligibility and
            ask him or her to submit your enrollment certification to VA. After your school submits your enrollment
            certification, your tuition and fees payment will be sent to the school on your behalf. All other payments
            will be sent directly to you.
        </p>
    </div>

    <h2 style="margin:25px 0 5px 0;">Other Information</h2>
    <div>
        <p>
            You should promptly notify your school's veterans certifying official and VA if there is any change in your
            enrollment. Generally, we can't pay you for:
        </p>
        <ul style="list-style-type:disc">
            <li style="margin-left:20px;">
                Courses you don't attend.
            </li>
            <li style="margin-left:20px;">
                Courses from which you withdraw.
            </li>
            <li style="margin-left:20px;">
                Courses you complete but receive a grade which will not count towards graduation.
            </li>
        </ul>

        <p style="margin:15px auto">
        <table class="tg" width="80%" style="border:1px solid;">
            <tr>
                <td style="font-size:75%;font-weight:bold;text-align:center;">
                    You are responsible for <span style="font-style: italic;text-decoration: underline;">ALL</span>
                    debts resulting from reductions or terminations of your enrollment even if the payment was submitted
                    directly to the school on your behalf.
                </td>
            </tr>
        </table>
        </p>

        <p>
            You may notify VA via:
        </p>
        <ul style="list-style-type:disc">
            <li style="margin-left:20px;">
                The Internet by visiting www.GIBILL.va.gov
            </li>
            <li style="margin-left:20px;">
                Telephone by calling toll-free at 1-888-GI-BILL-1 (1-888-442-4551).
            </li>
            <li style="margin-left:20px;">
                Postal mail by sending correspondence to the address at the top of this letter.
            </li>
        </ul>
    </div>

    <h2 style="margin:25px 0 5px 0;">If You Have Questions or Need Assistance</h2>
    <div>
        <p>
            If you have questions or need assistance, contact the Department of Veterans Affairs at 1-888-GI-BILL-1
            (1-888-442-4551). If you use the Telecommunications Device for the Deaf (TDD), the Federal number is 711.
            See the "If You Need Help" enclosure for contact information.
        </p>
    </div>
</div>

<div id="signature_container" style="margin:25px 0 0 0;">
    <table class="tg">
        <caption class="SR_only" style="color:#ffffff;font-size:1px">Layout Table of Signature</caption>
        <tbody>
        <tr>
            <th/>
            <th/>
        </tr>
        <tr class="row_signature">
            <td class="col_signature">
                Sincerely, <br/><br/>
                <img src="common/kwagner-signature.gif" title="Image of Kim Wagner Signature"
                     alt="Image of Kim Wagner Signature" width="60%" height="60%"/><br/><br/>
                Kim Wagner <br/>
                Education Officer

                <table class="tg" style="margin-left:0;">
                    <caption class="SR_only" style="color:#ffffff;font-size:1px">Layout Table of Enclosure</caption>
                    <tbody>
                    <tr>
                        <th/>
                        <th/>
                    </tr>
                    <tr>
                        <td valign="top" style="padding-left:0px;">
                            Enclosures: <br/>
                            <br/>
                        </td>
                        <td>
                            VA Form 4107 <br/>
                            If You Need Help
                        </td>
                    </tr>
                    </tbody>
                </table>
            </td>
        </tr>
        </tbody>
    </table>
</div>

<h1 class="SR_only" style="color:#ffffff;font-size:1px;page-break-before:always">
    YOUR RIGHTS TO APPEAL OUR DECISION (Page 01)
</h1>

<div id="rights_container01" style="font-size:14px;">
    <table class="tg" width="95%" style="border:2px solid;border-collapse: collapse;">
        <caption class="SR_only" style="color:#ffffff;font-size:1px">Layout Table of Rights Appeal (Page 01)</caption>
        <tbody>
        <tr>
            <th style="background:#000000;border:2px solid;border-top: 0px;border-left: 0px;width:20%;">
                <img src="common/DOVA-logo.gif" title="Image of Department of Veteran Affairs logo"
                     alt="Image of Department of Veteran Affairs logo"/>
            </th>
            <th style="border:2px solid;border-top: 0px;border-right: 0px;font-size:20px;font-weight: bold;text-align:center;font-family: Sans-Serif;width:79.15%;">
                YOUR RIGHTS TO APPEAL OUR DECISION
            </th>
        </tr>
        <tr class="row_letter">
            <td class="col_letter" colspan="2" style="padding:0 20px 20px 20px;">
                <div>
                    <p></p>
                    <div>
                        After careful and compassionate consideration, a decision has been reached on your claim. If we
                        were not able to grant some or all of the VA benefits you asked for, this form will explain what
                        you can do if you disagree with our decision. If you do not agree with our decision, you may:
                    </div>

                    <ol>
                        <li style="margin-left:20px;">
                            Start an appeal by submitting a Notice of Disagreement.
                        </li>
                        <li style="margin-left:20px;">
                            Give us evidence we do not already have that may lead us to change our decision.
                        </li>
                    </ol>

                    <p>
                        This form will tell you how to appeal to the Board and how to send us more evidence. You can do
                        either one or both of these things.
                    </p>
                </div>

                <h2 style="font-size:16px;text-align:center;margin-bottom:5px">HOW CAN I APPEAL THE DECISION?</h2>
                <div>
                    <p>
                        <strong>How do I start my appeal?</strong> To begin your appeal, you <strong>must</strong>
                        submit VA Form 21-0958, "Notice of Disagreement," if that form was provided to you in connection
                        with our decision. If we denied more than one claim for a benefit (for example, if you claimed
                        compensation for three disabilities and we denied two of them), please tell us in Part IV of VA
                        Form 21-0958 each of the claims you are appealing. A filed VA Form 21-0958 is considered your
                        Notice of Disagreement. If you did not receive VA Form 21-0958 in connection with our decision,
                        then write us a letter telling us you disagree with our decision or enter your disagreement on
                        VA Form 21-0958 in questions 11 or 12A. If you did not receive VA Form 21-0958 in connection
                        with our decision, then either your statement or VA Form 21-0958 is considered your Notice of
                        Disagreement. <strong>Send your Notice of Disagreement to the address included on our decision
                            notice letter.</strong>
                    </p>
                    <p>
                        <strong>How long do I have to start my appeal? You have one year to start an appeal of our
                            decision. Your</strong> Notice of Disagreement must be postmarked (or received by us) within
                        one year from the date of <strong>our</strong> letter denying you the benefit. In most cases,
                        you cannot appeal a decision after this one-year period has ended.
                    </p>
                    <p></p>
                    <div>
                        <strong>What happens if I do not start my appeal on time?</strong> If you do not start your
                        appeal on time, our decision will become final. Once our decision is final, you cannot get the
                        VA benefit we denied unless you either:
                    </div>
                    <ol>
                        <li style="margin-left:20px;">
                            Show that we were clearly wrong to deny the benefit <strong>or</strong>
                        </li>
                        <li style="margin-left:20px;">
                            Send us new evidence that relates to the reason we denied your claim.
                        </li>
                    </ol>
                    <p>
                        <strong>What happens after VA receives my Notice of Disagreement?</strong> We will either grant
                        your claim or send you a Statement of the Case. A Statement of the Case describes the facts,
                        laws, regulations, and reasons that we used to make our decision. We will also send you a VA
                        Form 9, "Appeal to Board of Veterans' Appeals," with the Statement of the Case. If you want to
                        continue your appeal to the Board of Veterans' Appeals (Board) after receiving a Statement of
                        the Case, you must complete and return the VA Form 9 within one year from the date of our letter
                        denying you the benefit or within 60 days from the date that we mailed the Statement of the Case
                        to you, <strong>whichever is later.</strong> If you decide to complete an appeal by filing a VA
                        Form 9, you have the option to request a Board hearing. Hearings often increase wait time for a
                        Board decision. It is not necessary for you to have a hearing for the Board to decide your
                        appeal. It is your choice.
                    </p>
                    <p></p>
                    <div>
                        <strong>Where can I find out more about the VA appeals process?</strong>
                    </div>
                    <ol>
                        <li style="margin-left:20px;">
                            You can find a "plain language" pamphlet called "How Do I Appeal," on the Internet at: <a
                                    href="http://www.bva.va.gov/How_Do_I_Appeal.asp"
                                    title="click to go to www.bva.va.gov/How_Do_I_Appeal.asp" target="_blank">http://www.bva.va.gov/How_Do_I_Appeal.asp</a>.
                        </li>
                        <li style="margin-left:20px;">
                            You can find the formal rules for the VA appeals process in title 38, Code of Federal
                            Regulations, Part 20. You can find the complete Code of Federal Regulations on the Internet
                            at: <a href="http://www.ecfr.gov" title="click to go to www.ecfr.gov" target="_blank">http://www.ecfr.gov</a>.
                            A printed copy of the Code of Federal Regulations may be available at your local law
                            library.
                        </li>
                    </ol>
                </div>

                <h2 style="font-size:16px;text-align:center;margin-bottom:5px;page-break-before:always">YOUR RIGHT TO
                    REPRESENTATION</h2>
                <div>
                    <p></p>
                    <div>
                        <strong>Can I get someone to help me with my appeal?</strong> Yes. You can have a Veterans
                        Service Organization representative, an attorney-at-law, or an "agent" help you with your
                        appeal. You are not required to have someone represent you. It is your choice.
                    </div>
                    <ol>
                        <li style="margin-left:20px;">
                            Representatives who work for accredited Veterans Service Organizations know how to prepare
                            and present claims and will represent you. You can find a listing of these organizations on
                            the Internet at: <a href="http://www.va.gov/vso"
                                                title="click to go to http://www.va.gov/vso" target="_blank">http://www.va.gov/vso</a>.
                        </li>
                    </ol>
                </div>
            </td>
        </tr>
        </tbody>
    </table>

    <table class="tg" width="95%">
        <caption class="SR_only" style="color:#ffffff;font-size:1px">Layout Table of Rights Appeal Footer (Page 01)
        </caption>
        <tbody>
        <tr>
            <th/>
            <th/>
            <th/>
        </tr>
        <tr>
            <td style="font-size:10px;width:10%;">
                VA FORM <br/>
                JUN 2016
            </td>
            <td style="width:34%;font-size:20px;font-weight:bold;">
                4107
            </td>
            <td style="font-size:10px;width:56%;">
                (Please continue reading on page 2)
            </td>
        </tr>
        </tbody>
    </table>
</div>

<h1 class="SR_only" style="color:#ffffff;font-size:1px">
    YOUR RIGHTS TO APPEAL OUR DECISION (Page 02)
</h1>

<div id="rights_container01" style="margin:10px 0;font-size:14px;">
    <table class="tg" width="95%" style="border:2px solid;border-collapse: collapse;">
        <caption class="SR_only" style="color:#ffffff;font-size:1px">Layout Table of Rights Appeal (Page 02)</caption>
        <tbody>
        <tr>
            <th/>
            <th/>
        </tr>
        <tr class="row_letter">
            <td class="col_letter" colspan="2" style="padding:0 20px 20px 20px;">
                <div>
                    <ol>
                        <li style="margin-left:20px;">
                            A private attorney or an "agent" can also represent you. VA only recognizes attorneys who
                            are licensed to practice in the United States or in one of its territories or possessions.
                            Your local bar association may be able to refer you to an attorney with experience in
                            veterans' law. An agent is a person who is not a lawyer, but who VA recognizes as being
                            knowledgeable about veterans' law. Contact us if you would like to know if there is a VA
                            accredited agent in your area.
                        </li>
                    </ol>
                    <p></p>
                    <div>
                        <strong>Do I have to pay someone to help me with my appeal to the Board?</strong> It depends on
                        who helps you. The following explains the differences.
                    </div>
                    <ol>
                        <li style="margin-left:20px;">
                            Veterans' service organizations will represent you for free.
                        </li>
                        <li style="margin-left:20px;">
                            Attorneys or agents can charge you for helping you under some circumstances. Paying their
                            fees for helping you with your appeal is your responsibility. If you do hire an attorney or
                            agent to represent you, a copy of any fee agreement must be sent to VA. The fee agreement
                            must clearly specify if VA is to pay the attorney or agent directly out of past-due
                            benefits. See 38 C.F.R. Â§ 14.636(g)(2). If the fee agreement provides for the direct
                            payment of fees out of past-due benefits, a copy of the direct- pay fee agreement must be
                            filed with us at the address included on our decision notice letter within 30 days of its
                            execution. A copy of any fee agreement that is not a direct-pay fee agreement must be filed
                            with the Office of the General Counsel within 30 days of its execution by mailing the copy
                            to the following address: Office of the General Counsel (022D), Department of Veterans
                            Affairs, 810 Vermont Avenue, NW., Washington, DC 20420. See 38 C.F.R. Â§ 14.636(g)(3).
                        </li>
                    </ol>
                </div>

                <h2 style="font-size:16px;text-align:center;margin-bottom:5px;page-break-before:always">GIVING VA
                    ADDITIONAL EVIDENCE?</h2>
                <div>
                    <p>
                        You can send us more evidence to support a claim whether or not you choose to appeal.
                    </p>
                    <p>
                        <strong>NOTE: Please direct all new evidence to the address included on our decision notice
                            letter. You should not send evidence directly to the Board at this time. You should only
                            send evidence to the Board if you decide to complete an appeal and, then, you should only
                            send evidence to the Board after you receive written notice from the Board that they
                            received your appeal.</strong>
                    </p>
                    <p>
                        If you have more evidence to support a claim, it is in your best interest to give us that
                        evidence as soon as you can. We will consider your evidence and let you know whether it changes
                        our decision. Please keep in mind that we can only consider new evidence that: (1) we have not
                        already seen and (2) relates to your claim. You may give us this evidence either in writing or
                        at a personal hearing with your local VA office.
                    </p>
                    <p>
                        <strong>In writing.</strong> To support your claim, you may send documents and written
                        statements to us at the address included on our decision notice letter. Tell us in a letter how
                        these documents and statements should change our earlier decision.
                    </p>
                    <p></p>
                    <div>
                        <strong>At a personal hearing.</strong> You may request a hearing with an employee at your local
                        VA office at any time, whether or not you choose to appeal. We do not require you to have a
                        local hearing. It is your choice. At this hearing, you may speak, bring witnesses to speak on
                        your behalf, and hand us written evidence. If you want a local hearing, send us a letter asking
                        for a local hearing. Use the address included on our decision notice letter. We will then:
                    </div>
                    <ol>
                        <li style="margin-left:20px;">
                            Arrange a time and place for the hearing
                        </li>
                        <li style="margin-left:20px;">
                            Provide a room for the hearing
                        </li>
                        <li style="margin-left:20px;">
                            Assign someone to hear your evidence
                        </li>
                        <li style="margin-left:20px;">
                            Make a written record of the hearing
                        </li>
                    </ol>
                </div>

                <h2 style="font-size:16px;text-align:center;margin-bottom:5px">WHAT HAPPENS AFTER I GIVE VA
                    EVIDENCE?</h2>
                <div>
                    <p>
                        We will review any new evidence, including the record of the local hearing, if you choose to
                        have one, together with the evidence we already have. We will then decide if we can grant your
                        claim. If we cannot grant your claim and you complete an appeal, we will send the new evidence
                        and the record of any local hearing to the Board.
                    </p>
                </div>
            </td>
        </tr>
        </tbody>
    </table>

    <table class="tg" width="95%">
        <caption class="SR_only" style="color:#ffffff;font-size:1px">Layout Table of Rights Appeal Footer (Page 02)
        </caption>
        <tbody>
        <tr>
            <th/>
            <th/>
        </tr>
        <tr>
            <td style="font-size:10px;width:44%;">
                BACK OF VA FORM 4107, JUN 2016
            </td>
            <td style="font-size:10px;width:56%;">
                SUPERCEDES VA FORM 4107, JUN 2015, <br/>
                WHICH WILL NOT BE USED
            </td>
        </tr>
        </tbody>
    </table>
</div>

<h1 class="SR_only" style="color:#ffffff;font-size:1px">
    IF YOU NEED HELP (Page 01)
</h1>

<div id="rights_container01" style="margin:10px 0;font-size:14px;">
    <table class="tg" width="95%" style="border-collapse: collapse;">
        <caption class="SR_only" style="color:#ffffff;font-size:1px">Layout Table of Need Help (Page 01)</caption>
        <tbody>
        <tr>
            <th style="width:20%;">
                <img src="common/DOVAwhite-logo.gif" title="Image of White Department of Veteran Affairs logo"
                     alt="Image of White Department of Veteran Affairs logo"/>
            </th>
            <th/>
        </tr>
        <tr class="row_letter">
            <td class="col_letter" colspan="2" style="padding:0 20px 20px 20px;">
                <h2 style="border-bottom:2px solid;font-family: Sans-Serif;text-align:center;">IF YOU NEED HELP</h2>
                <p style="border-bottom:2px solid;margin-top:-10px;"></p>
                <div>
                    <p>
                        If you need help with your VA education benefits, you can contact us in the following ways:
                    </p>
                    <p style="text-align:center;width:100%;">
                        <img src="common/world.gif" title="Image of The World" alt="Image of The World" width="360"
                             height="138"/>
                    </p>
                    <p>
                        VA has a national education Home Page on the World Wide Web (internet) where you can get
                        information about VA education benefit programs. The National Home Page address is:
                    </p>
                    <p style="text-align:center;width:100%;">
                        <a href="http://www.benefits.va.gov/gibill" title="click to go to www.benefits.va.gov/gibill"
                           target="_blank">http://www.benefits.va.gov/gibill</a>
                    </p>
                    <p>
                        You can ask a question about your education claim by choosing the â€œSubmit a Questionâ€� option
                        from the home page, and following the instructions on the screen.
                    </p>
                    <p style="border-bottom:1px solid;"></p>

                    <p style="text-align:center;width:100%;">
                        <img src="common/phone.gif" title="Image of phone" alt="Image of phone" width="232"
                             height="127"/>
                    </p>
                    <p>
                        If you need help with your VA education benefits, you can call toll-free from the U.S. by
                        dialing <strong>1-888-GI-BILL-1</strong> (1-888-442-4551.) If you use the Telecommunications
                        Device for the Deaf (TDD), the Federal Relay number is 711.

                    </p>
                    <p style="border-bottom:1px solid;"></p>

                    <p style="text-align:center;width:100%;">
                        <img src="common/mail.gif" title="Image of mailbox" alt="Image of mailbox" width="168"
                             height="176"/>
                    </p>
                    <p>
                        You can <strong>mail</strong> inquiries or claims for education benefits to your Regional
                        Processing Office. See the back of this sheet for instructions.

                    </p>
                    <p style="border-bottom:1px solid;"></p>
                </div>
            </td>
        </tr>
        </tbody>
    </table>

    <table class="tg" width="95%" style="page-break-before:always">
        <caption class="SR_only" style="color:#ffffff;font-size:1px">Layout Table of Rights Appeal Footer (Page 01)
        </caption>
        <tbody>
        <tr>
            <th/>
            <th/>
            <th/>
        </tr>
        <tr>
            <td style="font-size:10px !important;width:10%;">
                VA FORM <br/>
                JAN 2014
            </td>
            <td style="width:34%;font-size:20px;font-weight:bold;">
                22-0338
            </td>
            <td style="font-size:10px !important;width:56%;">
                EXISTING STOCKS OF VA FORM 22-0338, JUL 2012,<br/>
                WILL NOT BE USED.
            </td>
        </tr>
        </tbody>
    </table>
</div>

<h1 class="SR_only" style="color:#ffffff;font-size:1px">
    IF YOU NEED HELP (Page 02)
</h1>

<div id="rights_container01" style="margin:10px 0;font-size:14px;">
    <table class="tg" width="95%" style="border-collapse: collapse;">
        <caption class="SR_only" style="color:#ffffff;font-size:1px">Layout Table of Need Help (Page 02)</caption>
        <tbody>
        <tr>
            <th/>
            <th/>
        </tr>
        <tr class="row_letter">
            <td class="col_letter" colspan="2" style="padding:0 20px 20px 20px;">
                <h2 style="font-size:16px;margin-bottom:5px">Which VA Office Handles Your Education Claim?</h2>
                <div>
                    <p>
                        There are four regional education processing offices that handle claims for the entire country,
                        which we have divided into regions. The map below shows the states in each region. Find the
                        state where you'll be attending school or job training. You should <strong>mail</strong>
                        inquiries or claims for education benefits to the processing office for that region.
                    </p>
                    <p style="text-align:center;width:100%;">
                        <img src="common/usamap.gif" title="Image of map of USA" alt="Image of map of USA" width="500"
                             height="261"/>
                    </p>
                </div>
            </td>
        </tr>
        </tbody>
    </table>

    <table class="tg" width="95%">
        <caption class="SR_only" style="color:#ffffff;font-size:1px">Layout Table of Regional addresses</caption>
        <tbody>
        <tr>
            <th/>
            <th/>
        </tr>
        <tr>
            <td style="padding:20px;width:50%;">
                <span style="text-decoration: underline;">CENTRAL REGION:</span> <br/>
                VA Regional Office <br/>
                PO Box 66830 <br/>
                St. Louis, MO 63166-6830
            </td>
            <td style="padding:20px;width:50%;">
                <span style="text-decoration: underline;">EASTERN REGION:</span> <br/>
                VA Regional Office <br/>
                PO Box 4616 <br/>
                Buffalo, NY 14240-4616
            </td>
        </tr>
        <tr>
            <td style="padding:20px;width:50%;">
                <span style="text-decoration: underline;">WESTERN REGION:</span> <br/>
                VA Regional Office <br/>
                PO Box 8888 <br/>
                Muskogee, OK 74402-8888
            </td>
            <td style="padding:20px;width:50%;">
                <span style="text-decoration: underline;">SOUTHERN REGION:</span> <br/>
                VA Regional Office <br/>
                PO Box 100022 <br/>
                Decatur, GA 30031-7022
            </td>
        </tr>
        </tbody>
    </table>

    <table class="tg" width="95%">
        <caption class="SR_only" style="color:#ffffff;font-size:1px">Layout Table of Rights Appeal Footer (Page 01)
        </caption>
        <tbody>
        <tr>
            <th/>
            <th/>
            <th/>
        </tr>
        <tr>
            <td style="font-size:10px;width:25%;">
                VA FORM 22-0338, JAN 2014
            </td>
        </tr>
        </tbody>
    </table>
</div>
</body>
</html>