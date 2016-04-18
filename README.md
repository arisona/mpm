Multi-Projector-Mapper (MPM)
============================

Java / OpenGL based framework for multi-projector 3D mapping and more.

NOTE: This code is not developed any longer. It has been integrated in our ether framework. Work in progress is at https://github.com/arisona/ether


Overview
--------

The multi-projector-mapper (MPM) is a software framework for 3D projection mapping using multiple projectors. It contains a basic rendering infrastructure, and interactive tools for projector calibration. For calibration, the method given in Oliver Bimber and Ramesh Raskar's book "Spatial Augmented Reality" (http://www.amazon.com/Spatial-Augmented-Reality-Merging-Virtual/dp/1568812302), Appendix A, is used.

The framework is the outcome of the "Projections of Reality" cluster at smartgeometry 2013 (http://www.smartgeometry.org/), and is to be seen as a prototype that can be used for developing specialized projection mapping applications. Alternatively, the projector calibration method alone could also be used just to output the OpenGL projection and modelview matrices, which then can be used by other applications. In addition, the more generic code within the framework might as well serve as a starting point for those who want to dive into 'pure' Java / OpenGL coding (e.g. when coming from Processing).

Currently, at ETH Zurich's Future Cities Laboratory (http://www.futurecities.ethz.ch) we continue to work on the code. Among upcoming features will be the integration of the 3D scene analysis component, that was so far realised by a separate application. Your suggestions and feedback are welcome!



Repository
----------

The repository contains an Eclipse project, including dependencies such as JOGL etc. Thus the code should run out of the box on Mac OS X, Windows and Linux.


Further Info & Contact
----------------------

For an introduction and tutorial, go to http://robotized.arisona.ch/mpm/

A general overview of the "Projections of Reality" work is at http://robotized.arisona.ch/projections-of-reality/

Also, there's a short video documentation at https://vimeo.com/65130490

For questions etc. feel free to be in touch with me (Stefan Müller Arisona) at robot@arisona.ch


Credits
-------

Concept & Setup: Eva Friedrich & Stefan Müller Arisona

Code: MPM was written by Stefan Müller Arisona, with contributions by Eva Friedrich (early prototyping, and shadow volumes) and Simon Schubiger (OSC).

Support: This software was developed in part at ETH Zurich's Future Cities Laboratory in Singapore.
