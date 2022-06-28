let extrImageCount = 0;
$(document).ready(function() {

	$("#shortDescription").richText();
	$("#fullDescription").richText();

	getCategoriesForNew();
	brandsDropdown.change(function() {
		categoriesDropdown.empty();
		getCategories();
	});


	$("input[name='extraImage']").each(function(index) {

		$(this).change(function() {
			showExtraImageThumbnail(this, index);
		});
});


 	$('#myTab a').on('click', function(e) {
		e.preventDefault()
		$(this).tab('show')
	});

	$('form').submit(function(e) {
		e.preventDefault();
		checkUnique(this);
	});
	
	$("a[name='linkRemoveExtraImage']").each(function(index){
		
		$(this).click(function(){
			removeExtraImage(index);
		});
		
	});
	
	$("a[name='linkRemoveDetail']").each(function(index){
		$(this).click(function(){
			removeDetailSectionById(index);
		});
		
	});

});

function checkUnique(form) {

	const id = $("#id").val();
const name = $("#name").val();

const csrfValue = $("input[name='_csrf']").val();
	const params = { id: id,  name: name, _csrf: csrfValue };

	$.post(checkUniqueUrl, params, function(response) {
		if (response == "OK") {

			form.submit()
		} else if (response == "Duplicate") {
			showWWarningModal("There is another Product having same name " + name);
		} else {
	showErrorModal("Unkown error from server");
		}
	}).fail(function() {
		showErrorModal("Can't connect to server");
	});

	return false;
}

	function getCategoriesForNew() {

		const catId = $("#categoryId");
		let editMode = false;
		if (catId.length) editMode = true;
		if(!editMode) getCategories();
	}
function showExtraImageThumbnail(input, index) {

	const file = input.files[0];
	const fileName = file.name;
	
	const imageNameOfHiddenField = $("#imageName" + index);
	if(imageNameOfHiddenField.length){
		imageNameOfHiddenField.val(fileName);
	}
	
	const reader = new FileReader();

	reader.onload = function(evt) {
		$("#extra-thumbnail" + index).attr("src", evt.target.result);
	};
	reader.readAsDataURL(file);
	if (index >= extrImageCount - 1) {
		addExtraImageSection(index + 1);
	}
}

function addExtraImageSection(index) {
	const html = `
			<div class="col border m-3 p-2" id="divExtraImage${index}">
				<div id="extraImageHeader${index}"><label>Extra Image #${index + 1}: </label></div>
				<div>
					<img id="extra-thumbnail${index}"  alt = "Extra Image #${index + 1} preview" class="image-fluid" 
					src="${defaultImage}" style="width: 200px; height: 125px;"
					accept="image/png, image/jpeg"/>
				</div>
				
				<div>
					<input type="file" id="extra-image${index}" name="extraImage" onchange="showExtraImageThumbnail(this, ${index})"/>
				</div>
			</div>`;

	const htmlRemoveLink = `<a class="fas fa-times-circle fa-2x icon-dark" href="javascript:removeExtraImage(${index - 1})"
	style="float: right;" 
	title="Remove this image"></a>`;

	$("#divProductImages").append(html);
	$("#extraImageHeader" + (index - 1)).append(htmlRemoveLink);
	extrImageCount++;
}

function removeExtraImage(index) {

	$("#divExtraImage" + index).remove();
}


function addNextDetailSection() {
	const allDivDetails = $("[id^='divDetail']");
	const allDivDetailsCount = allDivDetails.length;

	const html = `
		<div class="form-group row" id="divDetail${allDivDetailsCount}">
					<input type="hidden" name="detailsIDs" value="0" />

			<div class="col-md-4">
				<label class="m-3 col-md-4">Name:</label>
				<input type="text" class="form-control col-md-8" name="detailNames" maxlength="255" />
			</div>
			<div class="col-md-4">
				<label class="m-3 col-md-4">Value:</label>
				<input type="text" class="form-control col-md-8" name="detailValues" maxlength="255" />
			</div>
		</div>
	`;

	$("#product-details").append(html);

	const lastDetailSection = allDivDetails.last();
	const lastDetailSectionId = lastDetailSection.attr("id");

	const htmlRemoveLink = `<a class="fas fa-times-circle fa-2x icon-dark col-md-2" style="float: right; margin-top: 59px" 
	href="javascript:removeDetailSectionById('${lastDetailSectionId}')"
	title="Remove this detail"></a>`;

	lastDetailSection.append(htmlRemoveLink);

	$("input[name='detailNames']").last().focus();

}

function getCategories() {
		$.get(getCategoriesUrl, function(response) {
			$.each(response, function(index, category) {
				$("<option>").val(category.id).text(category.name).appendTo(categoriesDropdown);

			});
		});
	}


function removeDetailSectionById(id) {
	$("#" + id).remove();
}