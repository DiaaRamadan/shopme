const countryList = $("#country");
const dataListStates = $("#statesList");
const stateField = $("#state");

$(document).ready(function() {
	countryList.on("change", function() {
		loadStatesForCountry();
		stateField.val("").focus();
	});

	$("#createCustomerForm").submit(function(e) {
		e.preventDefault();
		checkEmailUnique(this);
	})
})

function loadStatesForCountry() {
	const selectedCountry = $("#country option:selected");
	const countryId = selectedCountry.val();
	const url = contextPath + "settings/states/list-states-by-country/" + countryId;

	$.get(url, function(responseJson) {
		dataListStates.empty();
		$.each(responseJson, function(index, state) {
			$("<option>").val(state.name).text(state.name).appendTo(dataListStates);
		});
	}).fail(function() {
		alert("Failed to fetch data from server");
	});

}


function checkPasswordMatch(confirmPassword) {
	if (confirmPassword.value != $("#password").val())
		confirmPassword.setCustomValidity("Passwords not matches");
	else
		confirmPassword.setCustomValidity("");
}


function checkEmailUnique(form) {
	const email = $("#email").val();
	const url = contextPath + "customer/email/check-unique?email=" + email;
	$.get(url, function(response) {
		if (response == "OK") {
			form.submit();
		} else if (response == "Duplicate") {
			alert("Email already exist");
		} else {
			alert("some errors happens");
		}
	}).fail(function() {
		alert("Server error, while fetch data");
	});
	return false;
}
