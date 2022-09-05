$(document).ready(function() {

	$("#add2CartButton").on("click", function(evt) {
		addToCart();
	});

});

function addToCart() {
	const quantity = $("#quantity" + productId);
	const url = contextPath + "cart/add/" + productId + "/" + quantity.val();

	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response) {
		alert(response);
	}).fail(function(reposne) {
		console.log(reposne)
		alert("Error while adding product to cart");
	});
}