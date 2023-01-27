@drinks
Feature: Drinks
  I want to use this template for my feature file

  @drinks
  Scenario Outline: Assestment 1
     Given I make a GET call to "<API>" to "<EndPoint>"
     Then I verify that status code is equal to <Code>
     And I verify below assets from the jasonpaths: "$.ingredients[0]." are exist and as required
     | idIngredient | String |
     | strIngredient | String |
     | strDescription | String |
     | strAlcohol | String |
     | strABV | String |
     And I verify consistency between assets: "$.ingredients[0].strAlcohol" and "$.ingredients[0].strABV"
     
     Examples: 
      | API  | EndPoint | Code |
      | todos | /v1 | 200 |
     
 
   @drinks
  Scenario Outline: Assestment 2
     Given I make a GET call to "<API>" to "<EndPoint>"
     Then I verify that status code is equal to <Code>
     And I verify below schema in "<EndPoint>" from the jasonarray: "$.drinks" is exist and as required
     | strDrink | notNullable |
     | strDrinkAlternate | Nullable |
     | strTags | notNullable |
     | strCategory | notNullable |
     | strIBA | Nullable |
     | strAlcoholic | notNullable |
     | strGlass | notNullable |
     | strInstructions | notNullable |
     | strDrinkThumb | Nullable |
     | strIngredient1 | notNullable |
     | strMeasure1 | notNullable |
     | strImageSource | Nullable |
     | strImageAttribution | Nullable |
     | strCreativeCommonsConfirmed | notNullable |
     | dateModified | notNullable |
     Examples: 
      | API  | EndPoint | Code |
      | api/json/v1/1/ | search.php?s=margarita | 200 |
      | api/json/v1/1/ | search.php?s=vodka | 200 |
      | api/json/v1/1/ | search.php?s=kola | 200 |
      
      
      
      
      