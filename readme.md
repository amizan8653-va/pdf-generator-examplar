This project is intended to show how to use apache PDF box to go and generate 508 compliant PDFs.

The PDF example here is relatively simple with only read elements (no input fields)

So just simple text, images, and tables. There's also a footer which is ignored by the screen reader except
for the very last footer of the last page. There is also a background image that's not read by screen reader.

To check the 508 compliance of a PDF generated, you can use this free tool.
A copy of this tool is also included in [this repo](accessibility-checker-tool/PAC_24.3.2.0.zip). Unfortunately... it's a windows only tool.
https://pac.pdf-accessibility.org/en



note: this entire thing was based off of a stack overflow post: 
https://stackoverflow.com/questions/49682339/how-can-i-create-an-accessible-pdf-with-java-pdfbox-2-0-8-library-that-is-also-v

note: if you'd like more details on the PDF spec, it's located at the link below. 
But the pdf was downloaded and included in [this repo](documents/PDF32000_2008.pdf) if this link goes down.
https://opensource.adobe.com/dc-acrobat-sdk-docs/pdfstandards/PDF32000_2008.pdf