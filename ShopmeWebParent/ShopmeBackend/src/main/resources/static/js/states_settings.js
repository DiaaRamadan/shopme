const loadCountriesForStatesBtn = $("#load-country-list-for-states");
const countriesListForStates = $("#country-list-for-states");
const statesList = $("#list-of-states");
const stateNameFiled = $("#state-name");
const addStateBtn = $("#add-state");
const updateStateBtn = $("#update-state");
const deleteStateBtn = $("#delete-state");

$(document).ready(function() {
	loadCountriesForStatesBtn.click(function() {
		loadCountrirsForStates();
	});

	countriesListForStates.on("change", function() {
		loadStatesForSelectedCountry();
	});

	statesList.on("change", function() {
		changeFormStateToSelectedState();
	});

	addStateBtn.click(function() {
		if (addStateBtn.val() == "Add") {
			addState();
		} else {
			changeFormStateToNewStates();
		}
	});

	updateStateBtn.click(function() {
		updateState();
	});

	deleteStateBtn.click(function() {
		deleteState();
	});
});

function deleteState() {
	const stateId = statesList.val();
	const url = contextPath + "states/delete/" + stateId;

	$.get(url, function() {
		$("#list-of-states option:selected").remove();
		changeFormStateToNewStates();
	}).done(function() {
		showToast("The state has been deleted");
	}).fail(function() {
		showToast("ERROR: Could not connect to the server or server encountered an error")
	});
}

function updateState() {
	const url = contextPath + "states/save";
	const countryName = $("#country-list-for-states option:selected").text();
	const countryId = countriesListForStates.val();
	const stateName = stateNameFiled.val();
	const stateId = statesList.val();
	const jsonData = { id: stateId, name: stateName, country: { id: countryId, name: countryName } };

	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: "application/json"
	}).done(function(stateId) {
		$("#list-of-states option:selected").val(stateId).text(stateName);
		showToast("The state has been updated.");
	}).fail(function() {
		showToast("ERROR: Could not connect to the server or server encountered an error");
	});
}

function addState() {
	const url = contextPath + "states/save";
	const countryName = $("#country-list-for-states option:selected").text();
	const countryId = countriesListForStates.val();
	const stateName = stateNameFiled.val();
	const jsonData = { name: stateName, country: { id: countryId, name: countryName } };

	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: "application/json"
	}).done(function(stateId) {
		selectNewlyAddedState(stateId, stateName);
		showToast("The state has been added.");
	}).fail(function() {
		showToast("ERROR: Could not connect to the server or server encountered an error");
	});
}

function selectNewlyAddedState(stateId, stateName) {
	$("<option>").val(stateId).text(stateName).appendTo(statesList);
	$("#country-list option[value='" + stateId + "']").prop("selected", true);
	countryCodeField.val("");
	countryNameField.val("");
}

function changeFormStateToNewStates() {
	addStateBtn.val("Add");
	stateNameFiled.val("").focus();
}

function changeFormStateToSelectedState() {
	stateNameFiled.val($("#list-of-states option:selected").text());
	updateStateBtn.prop("disabled", false);;
	deleteStateBtn.prop("disabled", false);

}

function loadStatesForSelectedCountry() {
	const countryId = countriesListForStates.val();
	const url = contextPath + "states/list-by-country/" + countryId;
	$.get(url, function(responseJson) {
		statesList.empty();
		$.each(responseJson, function(index, state) {
			$("<option>").val(state.id).text(state.name).appendTo(statesList);
		});
	}).done(function() {
		showToast("States have been loaded");
	}).fail(function() {
		showToast("ERROR: Could not connect to the server or server encountered an error")
	});
}

function loadCountrirsForStates() {
	const url = contextPath + "countries/list";
	$.get(url, function(responseJson) {
		countriesListForStates.empty();
		$.each(responseJson, function(index, country) {
			$("<option>").val(country.id).text(country.name).appendTo(countriesListForStates);
		});
	}).done(function() {
		loadStatesForSelectedCountry();
		loadCountriesForStatesBtn.val("Refresh Country List");
		showToast("All countries have been loaded");
	}).fail(function() {
		showToast("ERROR: Could not connect to the server or server encountered an error")
	});
}