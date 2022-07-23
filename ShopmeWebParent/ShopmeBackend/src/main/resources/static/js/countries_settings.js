const loadCountriesBtn = $('#load-country-list');
const countriesList = $('#country-list');
const addCountryBtn = $("#add-country");
const updateCountryBtn = $("#update-country");
const deleteCountryBtn = $("#delete-country");
const countryNameField = $("#country-name");
const countryCodeField = $("#country-code");


$(document).ready(function() {

	loadCountriesBtn.click(function() {
		loadCountriesList();
	});

	countriesList.on("change", function() {
		changeFormStateToSelectedCountry();
	});

	addCountryBtn.click(function() {
		if (addCountryBtn.val() == "Add") {
			addCountry();
		} else {
			changeFormStateToNew();
		}
	});

	updateCountryBtn.click(function() {
		updateCountry();
	});

	deleteCountryBtn.click(function() {
		deleteCountry();
	})

});

function deleteCountry() {
	const countryId = countriesList.val().split("-")[0];
	const url = contextPath + "countries/delete/" + countryId;
	
	$.get(url, function() {
		$("#country-list option:selected").remove();
		changeFormStateToNew();
	}).done(function() {
		showToast("The country has been deleted");
	}).fail(function() {
		showToast("ERROR: Could not connect to the server or server encountered an error")
	});
}

function updateCountry() {
	const url = contextPath + "countries/save";
	const countryName = countryNameField.val();
	const countryCode = countryCodeField.val();
	const countryId = countriesList.val().split("-")[0];
	const jsonData = { id: countryId, name: countryName, code: countryCode };

	$.ajax({

		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'

	}).done(function(countryId) {
		$("#country-list option:selected").val(countryId + "-" + countryCode).text(countryName);
		showToast("The country has been updated.");
	}).fail(function() {
		showToast("ERROR: Could not connect to the server or server encountered an error")
	});
}

function addCountry() {
	const url = contextPath + "countries/save";
	const countryName = countryNameField.val();
	const countryCode = countryCodeField.val();
	const jsonData = { name: countryName, code: countryCode };

	$.ajax({

		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'

	}).done(function(countryId) {
		showToast("New country added with ID: " + countryId);
		selectNewlyAddedCountry(countryId, countryName, countryCode);
	}).fail(function() {
		showToast("ERROR: Could not connect to the server or server encountered an error")
	});;
}

function selectNewlyAddedCountry(countryId, countryName, countryCode) {
	const optionValue = countryId + "-" + countryCode;
	$("<option>").val(optionValue).text(countryName).appendTo(countriesList);
	$("#country-list option[value='" + optionValue + "']").prop("selected", true);
	countryCodeField.val("");
	countryNameField.val("");
}

function changeFormStateToNew() {
	addCountryBtn.val("Add");

	updateCountryBtn.prop("disabled", true);
	deleteCountryBtn.prop("disabled", true);

	countryNameField.val("").focus();
	countryCodeField.val("");
}

function changeFormStateToSelectedCountry() {
	addCountryBtn.prop("value", "New");
	updateCountryBtn.prop("disabled", false);
	deleteCountryBtn.prop("disabled", false);

	const selectedCountry = $("#country-list option:selected").text();
	countryNameField.val(selectedCountry);
	const countryCode = countriesList.val().split("-")[1];
	countryCodeField.val(countryCode);
}

function loadCountriesList() {
	const url = contextPath + "countries/list";
	$.get(url, function(responseJson) {
		countriesList.empty();
		$.each(responseJson, function(index, country) {
			const optionValue = country.id + "-" + country.code;
			$("<option>").val(optionValue).text(country.name).appendTo(countriesList);
		});
	}).done(function() {
		loadCountriesBtn.val("Refresh Country List");
		showToast("All countries have been loaded");
	}).fail(function() {
		showToast("ERROR: Could not connect to the server or server encountered an error")
	});
}

function showToast(message) {
	$("#toastMessage").text(message);
	$('.toast').toast("show");
}