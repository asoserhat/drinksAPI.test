@todos
Feature: Todos
  I want to use this template for my feature file
  
  @todos
  Scenario Outline: Accumulus 1
     Given I make a GET call to "<API>" to "<EndPoint>"
     Then I verify that status code is equal to <Code>
     Then I size of the JSONArray is equal to <Size>
     And I verify response has todo values as required and contains "<String>"
     
     Examples: 
      | API  | EndPoint | Code | Size |String |
      | / | todosv2  | 200 | 50 | Finish the Accumulus coding challenge |
